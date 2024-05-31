package com.rc.ecommerce.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rc.ecommerce.exception.EComException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class PayHereConfig {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payHere.app.id}")
    private String appId;

    @Value("${payHere.app.secret}")
    private String appSecret;

    @Value("${payHere.merchant.id}")
    private String merchantId;

    @Value("${payHere.merchant.secret}")
    private String merchantSecret;

    @Value("${payHere.access.token}")
    private String accessToken;

    @Value("${payHere.checkout.url}")
    private String checkoutUrl;

    @Value("${payHere.return.url}")
    private String returnUrl;

    @Value("${payHere.cancel.url}")
    private String cancelUrl;

    @Value("${payHere.notify.url}")
    private String notifyUrl;

    @Value("${payHere.preApprove.url}")
    private String preApproveUrl;

    @Value("${payHere.oauth.token.url}")
    private String tokenUrl;

    @Value("${payHere.charging.api.url}")
    private String chargingUrl;

    @Value("${payHere.payment.detail.url}")
    private String paymentDetailUrl;

    @Value("${payHere.refund.url}")
    private String refundUrl;

    @Value("${payHere.capture.url}")
    private String captureUrl;

    public String getAccessToken() throws EComException {
        if (accessToken == null) {
            accessToken = retrieveAccessToken();
        }
        return accessToken;
    }

    public String retrieveAccessToken() throws EComException {
        String authCode = getAuthorizationCode();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authCode);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("grant_type", "client_credentials");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(bodyParams, headers);

        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

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
        String auth = appId + ":" + appSecret;
        return Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    public String generateHash(String orderId, double amount, String currency) {
        String hashedSecret = getMd5(merchantSecret.toUpperCase());
        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(amount);
        String data = merchantId + orderId + amountFormatted + currency + hashedSecret;
        return getMd5(data).toUpperCase();
    }

    public String generateHash(String merchantId, String orderId, double amount, String currency) {
        String hashedSecret = getMd5(merchantSecret.toUpperCase());
        String data = merchantId + orderId + amount + currency + hashedSecret;
        return getMd5(data).toUpperCase();
    }

    public String generateMd5Sig(String merchantId, String orderId, String amount, String currency, int statusCode) {
        String hashedSecret = getMd5(merchantSecret.toUpperCase());
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
            throw new RuntimeException(e);
        }
    }
}
