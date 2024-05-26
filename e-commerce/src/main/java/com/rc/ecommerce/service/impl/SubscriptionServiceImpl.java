package com.rc.ecommerce.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rc.ecommerce.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payhere.client-id}")
    private String clientId;

    @Value("${payhere.client-secret}")
    private String clientSecret;

    @Value("${payhere.token-url}")
    private String tokenUrl;

    @Value("${payhere.subscription-url}")
    private String subscriptionUrl;

    private String getAccessToken() throws Exception {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.path("access_token").asText();
        } else {
            throw new RuntimeException("Failed to retrieve access token");
        }
    }

    @Override
    public JsonNode viewAllSubscriptions() throws Exception {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(subscriptionUrl, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return objectMapper.readTree(response.getBody());
        } else {
            throw new RuntimeException("Failed to retrieve subscriptions");
        }
    }

    @Override
    public JsonNode viewPaymentsOfSubscription(String subscriptionId) throws Exception {
        String accessToken = getAccessToken();

        String url = UriComponentsBuilder.fromHttpUrl(subscriptionUrl)
                .pathSegment(subscriptionId, "payments")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return objectMapper.readTree(response.getBody());
        } else {
            throw new RuntimeException("Failed to retrieve subscription payments");
        }
    }

    @Override
    public JsonNode retrySubscription(String subscriptionId) throws Exception {
        String accessToken = getAccessToken();

        String url = subscriptionUrl + "/retry";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("subscription_id", subscriptionId);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return objectMapper.readTree(response.getBody());
        } else {
            throw new RuntimeException("Failed to retry subscription");
        }
    }

    @Override
    public JsonNode cancelSubscription(String subscriptionId) throws Exception {
        String accessToken = getAccessToken();

        String url = subscriptionUrl + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("subscription_id", subscriptionId);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return objectMapper.readTree(response.getBody());
        } else {
            throw new RuntimeException("Failed to cancel subscription");
        }
    }
}
