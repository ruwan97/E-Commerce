package com.rc.ecommerce.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rc.ecommerce.config.PayHereConfig;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.domain.Order;
import com.rc.ecommerce.model.domain.OrderItem;
import com.rc.ecommerce.model.domain.Payment;
import com.rc.ecommerce.model.dto.*;
import com.rc.ecommerce.model.enums.PaymentStatus;
import com.rc.ecommerce.repository.PaymentRepository;
import com.rc.ecommerce.service.OrderService;
import com.rc.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PayHereConfig payHereConfig;

    @Override
    public String generateHash(String orderId, double amount, String currency) {
        String hashedSecret = getMd5(payHereConfig.getMerchantSecret()).toUpperCase();
        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(amount);
        String data = payHereConfig.getMerchantId() + orderId + amountFormatted + currency + hashedSecret;
        return getMd5(data).toUpperCase();
    }

    @Override
    public String generateMd5Sig(String merchantId, String orderId, String amount, String currency, int statusCode) {
        String hashedSecret = getMd5(payHereConfig.getMerchantSecret()).toUpperCase();
        String data = merchantId + orderId + amount + currency + statusCode + hashedSecret;
        return getMd5(data).toUpperCase();
    }

    private String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void savePayment(PaymentNotificationDTO notificationDTO, String customerToken) throws EComException {
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

    private String hashCustomerToken(String customerToken) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(customerToken);
    }

    private void chargeCustomer(Payment payment) throws EComException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = constructRequestBody(payment);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    payHereConfig.getChargingUrl(),
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

    public String getAccessToken() throws EComException {
        if (payHereConfig.getAccessToken() == null) {
            payHereConfig.setAccessToken(retrieveAccessToken());
        }
        return payHereConfig.getAccessToken();
    }

    public String retrieveAccessToken() throws EComException {
        String authCode = getAuthorizationCode();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authCode);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("grant_type", "client_credentials");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(bodyParams, headers);

        ResponseEntity<String> response = restTemplate.exchange(payHereConfig.getTokenUrl(), HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            } catch (Exception e) {
                throw new EComException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to parse access token response");
            }
        } else {
            throw new EComException(response.getStatusCodeValue(), "Failed to retrieve access token");
        }
    }

    public String getAuthorizationCode() {
        String auth = payHereConfig.getAppId() + ":" + payHereConfig.getAppSecret();
        return Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
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

    @Override
    public JsonNode getPaymentDetails(String orderId) throws EComException {
        String accessToken = getAccessToken();

        String url = UriComponentsBuilder.fromHttpUrl(payHereConfig.getPaymentDetailUrl())
                .queryParam("order_id", orderId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readTree(response.getBody());
            } catch (Exception e) {
                throw new EComException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to parse payment details response");
            }
        } else {
            throw new EComException(response.getStatusCodeValue(), "Failed to retrieve payment details");
        }
    }

    @Override
    public RefundResponse refundPayment(RefundRequestDto refundRequest) throws EComException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RefundRequestDto> request = new HttpEntity<>(refundRequest, headers);
        ResponseEntity<RefundResponse> response = restTemplate.postForEntity(payHereConfig.getRefundUrl(), request, RefundResponse.class);

        return response.getBody();
    }

    @Override
    public CapturePaymentResponse capturePayment(CapturePaymentRequestDto request) throws EComException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = Map.of(
                "authorization_token", request.getAuthorizationToken(),
                "amount", request.getAmount(),
                "deduction_details", request.getDeductionDetails()
        );

        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(payHereConfig.getCaptureUrl(), httpRequest, Map.class);

            CapturePaymentResponse capturePaymentResponse = new CapturePaymentResponse();
            capturePaymentResponse.setStatus((int) Objects.requireNonNull(response.getBody()).get("status"));
            capturePaymentResponse.setMsg((String) response.getBody().get("msg"));
            capturePaymentResponse.setData((Map<String, Object>) response.getBody().get("data"));

            return capturePaymentResponse;
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error capturing payment: " + e.getMessage(), e);
        }
    }
}
