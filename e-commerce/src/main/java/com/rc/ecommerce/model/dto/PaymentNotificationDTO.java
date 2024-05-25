package com.rc.ecommerce.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentNotificationDTO {
    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("subscription_id")
    private String subscriptionId;

    @JsonProperty("payhere_amount")
    private String payhereAmount;

    @JsonProperty("payhere_currency")
    private String payhereCurrency;

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("md5sig")
    private String md5sig;

    @JsonProperty("custom_1")
    private String custom1;

    @JsonProperty("custom_2")
    private String custom2;

    @JsonProperty("method")
    private String method;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("recurring")
    private int recurring;

    @JsonProperty("message_type")
    private String messageType;

    @JsonProperty("item_recurrence")
    private String itemRecurrence;

    @JsonProperty("item_duration")
    private String itemDuration;

    @JsonProperty("item_rec_status")
    private int itemRecStatus;

    @JsonProperty("item_rec_date_next")
    private String itemRecDateNext;

    @JsonProperty("item_rec_install_paid")
    private int itemRecInstallPaid;

    @JsonProperty("customer_token")
    private String customerToken;

    @JsonProperty("card_holder_name")
    private String cardHolderName;

    @JsonProperty("card_no")
    private String cardNo;

    @JsonProperty("card_expiry")
    private String cardExpiry;
}
