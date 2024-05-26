package com.rc.ecommerce.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PayHereConfig {
    @Value("${payHere.app.id}")
    private String appId;

    @Value("${payHere.app.secret}")
    private String appSecret;

    @Value("${payHere.merchant.id}")
    private String merchantId;

    @Value("${payHere.access.token}")
    private String accessToken;

    @Value("${payHere.merchant.secret}")
    private String merchantSecret;

    @Value("${payHere.checkout.url}")
    private String checkoutUrl;

    @Value("${payHere.notify.url}")
    private String notifyUrl;

    @Value("${payHere.return.url}")
    private String returnUrl;

    @Value("${payHere.cancel.url}")
    private String cancelUrl;

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
}
