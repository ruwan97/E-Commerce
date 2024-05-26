package com.rc.ecommerce.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface SubscriptionService {
    JsonNode viewAllSubscriptions() throws Exception;

    JsonNode viewPaymentsOfSubscription(String subscriptionId) throws Exception;

    JsonNode retrySubscription(String subscriptionId) throws Exception;

    JsonNode cancelSubscription(String subscriptionId) throws Exception;
}
