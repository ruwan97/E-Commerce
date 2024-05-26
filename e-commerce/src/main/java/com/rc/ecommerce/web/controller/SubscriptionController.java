package com.rc.ecommerce.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.rc.ecommerce.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public JsonNode viewAllSubscriptions() {
        try {
            return subscriptionService.viewAllSubscriptions();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{subscriptionId}/payments")
    public JsonNode viewPaymentsOfSubscription(@PathVariable String subscriptionId) {
        try {
            return subscriptionService.viewPaymentsOfSubscription(subscriptionId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/retry")
    public JsonNode retrySubscription(@RequestParam String subscriptionId) {
        try {
            return subscriptionService.retrySubscription(subscriptionId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/cancel")
    public JsonNode cancelSubscription(@RequestParam String subscriptionId) {
        try {
            return subscriptionService.cancelSubscription(subscriptionId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
