package com.rc.ecommerce.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rc.ecommerce.config.PayHereConfig;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.domain.Order;
import com.rc.ecommerce.model.domain.Payment;
import com.rc.ecommerce.model.dto.*;
import com.rc.ecommerce.model.enums.PaymentStatus;
import com.rc.ecommerce.repository.PaymentRepository;
import com.rc.ecommerce.service.OrderService;
import com.rc.ecommerce.service.PaymentService;
import com.rc.ecommerce.web.controller.dto.ChargeRequest;
import com.rc.ecommerce.web.controller.dto.ChargeResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
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
    public void savePayment(SavePaymentDto savePaymentDto) {
        Order order = orderService.findByOrderId(savePaymentDto.getOrderId());
        if (order != null) {
            String hashedToken = hashCustomerToken(savePaymentDto.getCustomerToken());

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentId(savePaymentDto.getPaymentId());
            payment.setStatusCode(savePaymentDto.getStatusCode());
            payment.setStatusMessage(savePaymentDto.getStatusMessage());
            payment.setCardHolderName(savePaymentDto.getCardHolderName());
            payment.setCardNo(savePaymentDto.getCardNo());
            payment.setCardExpiry(savePaymentDto.getCardExpiry());
            payment.setCurrency(savePaymentDto.getCurrency());
            payment.setPaymentAmount(BigDecimal.valueOf(savePaymentDto.getAmount()));
            payment.setCustomerToken(hashedToken);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setPaidAt(new Date());
            payment.setCreatedAt(new Date());

            paymentRepository.save(payment);
        } else {
            logger.error("Order not found for orderId: {}", savePaymentDto.getOrderId());
            throw new IllegalArgumentException("Order not found for orderId: " + savePaymentDto.getOrderId());
        }
    }

    private String hashCustomerToken(String customerToken) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(customerToken);
    }

    @Override
    public JsonNode getPaymentDetails(String orderId) throws EComException {
        String accessToken = payHereConfig.getAccessToken();

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
        headers.setBearerAuth(payHereConfig.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RefundRequestDto> request = new HttpEntity<>(refundRequest, headers);
        ResponseEntity<RefundResponse> response = restTemplate.postForEntity(payHereConfig.getRefundUrl(), request, RefundResponse.class);

        return response.getBody();
    }

    @Override
    public CapturePaymentResponse capturePayment(CapturePaymentRequestDto request) throws EComException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + payHereConfig.getAccessToken());
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

    @Override
    public ChargeResponse chargeCustomer(ChargeRequest chargeRequest) throws EComException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(payHereConfig.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChargeRequest> requestEntity = new HttpEntity<>(chargeRequest, headers);

        ResponseEntity<ChargeResponse> responseEntity = restTemplate.exchange(
                payHereConfig.getChargingUrl(),
                HttpMethod.POST,
                requestEntity,
                ChargeResponse.class);

        ChargeResponse response = responseEntity.getBody();

        if (response != null) {
            if (response.getStatus() == 2) {
                // payment is success
                logger.debug("Payment charged successfully");

                // check if authorization token is present
                if (response.getData() != null && response.getData().getAuthorizationToken() != null) {
                    String authorizationToken = response.getData().getAuthorizationToken();
                    logger.debug("Authorization Token: {}", authorizationToken);
                }
            } else if (response.getStatus() == 0) {
                logger.debug("Payment is pending");
            } else if (response.getStatus() == -1) {
                logger.debug("Payment was canceled");
            } else if (response.getStatus() == -2) {
                logger.debug("Payment failed");
            } else {
                logger.debug("Unknown payment status code: {}", response.getStatus());
            }
        } else {
            logger.debug("Null response received");
        }

        return response;
    }

    private void updatePaymentInformation(ChargeRequest chargeRequest, ChargeResponse response) {
        Order order = orderService.findByOrderId(chargeRequest.getOrderId());
        if (order != null) {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentId(response.getData().getPaymentId());
            payment.setStatusCode(response.getStatus());
            payment.setStatusMessage(response.getMsg());
            payment.setCardHolderName(null);
            payment.setCardNo(null);
            payment.setCardExpiry(null);
            payment.setCurrency(response.getData().getCurrency());
            payment.setPaymentAmount(BigDecimal.valueOf(response.getData().getAmount()));
            payment.setCustomerToken(chargeRequest.getCustomerToken());
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setPaidAt(new Date());
            payment.setCreatedAt(new Date());

            paymentRepository.save(payment);
        }
    }
}
