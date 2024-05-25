package com.rc.ecommerce.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rc.ecommerce.model.domain.Order;
import com.rc.ecommerce.model.domain.OrderItem;
import com.rc.ecommerce.model.domain.Payment;
import com.rc.ecommerce.model.dto.PaymentNotificationDTO;
import com.rc.ecommerce.model.enums.PaymentStatus;
import com.rc.ecommerce.repository.PaymentRepository;
import com.rc.ecommerce.service.OrderService;
import com.rc.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payHere.charging.api.url}")
    private String chargingApiUrl;

    @Value("${payHere.oauth.token.url}")
    private String oauthTokenUrl;

    @Value("${payHere.access.token}")
    private String accessToken;

    @Value("${payHere.app.id}")
    private String appId;

    @Value("${payHere.app.secret}")
    private String appSecret;

    public String getAccessToken() {
        if (accessToken != null && !accessToken.isEmpty()) {
            return accessToken;
        } else {
            return retrieveAccessToken();
        }
    }

    public String retrieveAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(appId, appSecret);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                oauthTokenUrl,
                HttpMethod.POST,
                requestEntity,
                JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonNode responseBody = responseEntity.getBody();
            if (responseBody != null) {
                return responseBody.path("access_token").asText();
            }
        } else {
            logger.error("Failed to retrieve access token. Status code: {}", responseEntity.getStatusCode());
            return null;
        }
        return null;
    }

    @Override
    public void savePayment(PaymentNotificationDTO notificationDTO, String customerToken) {
        Order order = orderService.findOrderByOrderId(notificationDTO.getOrderId());
        if (order != null) {
            String hashedToken = hashCustomerToken(customerToken);

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentId(notificationDTO.getPaymentId());
            payment.setStatusCode(notificationDTO.getStatusCode());
            payment.setStatusMessage(notificationDTO.getStatusMessage());
            payment.setCardHolderName(notificationDTO.getCardHolderName());
            payment.setCardNo(notificationDTO.getCardNo());
            payment.setCardExpiry(notificationDTO.getCardExpiry());
            payment.setCurrency(notificationDTO.getPayhereCurrency());
            payment.setPaymentAmount(new BigDecimal(notificationDTO.getPayhereAmount()));
            payment.setCustomerToken(hashedToken);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setPaidAt(new Date());
            payment.setCreatedAt(new Date());

            paymentRepository.save(payment);

            // charge the customer
            chargeCustomer(payment);
        } else {
            logger.error("Order not found for orderId: {}", notificationDTO.getOrderId());
            throw new IllegalArgumentException("Order not found for orderId: " + notificationDTO.getOrderId());
        }
    }

    private void chargeCustomer(Payment payment) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = constructRequestBody(payment);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    chargingApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                handleChargingResponse(response.getBody(), payment);
            } else {
                logger.error("Charging API request failed with status code: {}", response.getStatusCode());
                //:TODO - handle error response
            }
        } catch (Exception e) {
            logger.error("Error occurred while calling Charging API: {}", e.getMessage());
            //:TODO - handle exception
        }
    }

    public String constructRequestBody(Payment payment) {
        ObjectNode rootNode = objectMapper.createObjectNode();

        rootNode.put("type", "PAYMENT");
        rootNode.put("order_id", payment.getOrder().getOrderId());
        rootNode.put("items", payment.getOrder().getItems());
        rootNode.put("currency", payment.getCurrency());
        rootNode.put("amount", payment.getPaymentAmount().doubleValue());
        rootNode.put("customer_token", payment.getCustomerToken());
        rootNode.put("custom_1", payment.getOrder().getCustom1());
        rootNode.put("custom_2", payment.getOrder().getCustom2());
        rootNode.put("notify_url", payment.getOrder().getNotifyUrl());

        ArrayNode itemList = objectMapper.createArrayNode();
        for (OrderItem orderItem : payment.getOrder().getOrderItems()) {
            ObjectNode itemNode = objectMapper.createObjectNode();
            itemNode.put("name", orderItem.getProduct().getName());
            itemNode.put("number", orderItem.getId());
            itemNode.put("quantity", orderItem.getQuantity());
            itemNode.put("unit_amount", orderItem.getPrice().doubleValue());
            itemList.add(itemNode);
        }

        rootNode.set("itemList", itemList);

        return rootNode.toString();
    }


    private void handleChargingResponse(String responseBody, Payment payment) {
        try {
            // parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // extract relevant information
            int statusCode = rootNode.path("status").asInt();
            String statusMessage = rootNode.path("msg").asText();

            // update payment status based on the response
            if (statusCode == 1) {
                // successful payment
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setStatusMessage(statusMessage);

            } else {
                // payment failed or other status
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setStatusMessage(statusMessage);

                logger.error("Payment failed with status code {}: {}", statusCode, statusMessage);
            }

            paymentRepository.save(payment);
        } catch (IOException e) {
            logger.error("Error parsing Charging API response: {}", e.getMessage());
            //:TODO - handle parsing error
        }
    }

    private String hashCustomerToken(String customerToken) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(customerToken);
    }
}
