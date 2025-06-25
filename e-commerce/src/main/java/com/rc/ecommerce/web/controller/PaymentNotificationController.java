package com.rc.ecommerce.web.controller;

import com.rc.ecommerce.model.enums.OrderStatus;
import com.rc.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment/notify")
public class PaymentNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentNotificationController.class);

    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<String> handlePaymentNotification(
            @RequestParam("merchant_id") String merchantId,
            @RequestParam("order_id") String orderId,
            @RequestParam("payhere_amount") String payHereAmount,
            @RequestParam("payhere_currency") String payHereCurrency,
            @RequestParam("status_code") String statusCode,
            @RequestParam("md5sig") String md5sig) {

        String localMd5sig = orderService.generateMd5Sig(merchantId, orderId, payHereAmount, payHereCurrency, statusCode);
        logger.debug("Received payment notification for order : {}", orderId);
        logger.debug("Received md5sig : {}", md5sig);
        logger.debug("Calculated md5sig : {}", localMd5sig);

        if (localMd5sig.equals(md5sig)) {
            if ("2".equals(statusCode)) {
                logger.info("Payment successful for order : {}", orderId);
                orderService.updateOrderAndPaymentDetails(orderId, payHereAmount, payHereCurrency, OrderStatus.SUCCESS);
                return ResponseEntity.ok("Payment successful for order: " + orderId);
            } else {
                logger.warn("Payment failed for order : {}", orderId);
                orderService.updateOrderAndPaymentDetails(orderId, payHereAmount, payHereCurrency, OrderStatus.FAILED);
                return ResponseEntity.badRequest().body("Payment failed for order: " + orderId);
            }
        } else {
            logger.error("MD5 signature verification failed for order : {}", orderId);
            return ResponseEntity.badRequest().body("MD5 signature verification failed for order: " + orderId);
        }
    }
}

