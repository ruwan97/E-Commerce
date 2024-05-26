package com.rc.ecommerce.web.controller;

import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.dto.PaymentNotificationDTO;
import com.rc.ecommerce.model.enums.OrderStatus;
import com.rc.ecommerce.service.OrderService;
import com.rc.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentNotificationController.class);

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping("/notify")
    public ResponseEntity<String> handleNotification(@RequestBody PaymentNotificationDTO notificationDTO) throws EComException {

        // validate request
        if (!isValidNotificationRequest(notificationDTO)) {
            return new ResponseEntity<>("Invalid payment notification request", HttpStatus.BAD_REQUEST);
        }

        String localMd5sig = paymentService.generateMd5Sig(notificationDTO.getMerchantId(), notificationDTO.getOrderId(), notificationDTO.getPayhereAmount(), notificationDTO.getPayhereCurrency(), notificationDTO.getStatusCode());
        logger.debug("Received payment notification for order : {}", notificationDTO.getOrderId());
        logger.debug("Received md5sig : {}", notificationDTO.getMd5sig());
        logger.debug("Calculated md5sig : {}", localMd5sig);

        // validate and process payment notification
        if (!localMd5sig.equals(notificationDTO.getMd5sig())) {
            return new ResponseEntity<>("MD5 signature verification failed", HttpStatus.BAD_REQUEST);
        }

        String customerToken = notificationDTO.getCustomerToken();
        if (customerToken != null) {
            // save payment
            paymentService.savePayment(notificationDTO, customerToken);
        } else {
            return new ResponseEntity<>("Customer token is null", HttpStatus.BAD_REQUEST);
        }

        if (notificationDTO.getStatusCode() == 2) {
            logger.info("Payment successful for order : {}", notificationDTO.getOrderId());

            // payment successful
            orderService.updateOrderStatus(notificationDTO.getOrderId(), OrderStatus.SUCCESS);
            return ResponseEntity.ok("Payment successful for order: " + notificationDTO.getOrderId());
        } else if (notificationDTO.getStatusCode() == -1) {
            logger.warn("Payment cancelled for order : {}", notificationDTO.getOrderId());

            // payment cancelled
            orderService.updateOrderStatus(notificationDTO.getOrderId(), OrderStatus.CANCELLED);
            return ResponseEntity.badRequest().body("Payment cancelled for order: " + notificationDTO.getOrderId());
        }
        return null;
    }

    private boolean isValidNotificationRequest(PaymentNotificationDTO notificationDTO) {
        if (notificationDTO == null) {
            return false;
        }

        return notificationDTO.getMerchantId() != null &&
                notificationDTO.getOrderId() != null &&
                notificationDTO.getPayhereAmount() != null &&
                notificationDTO.getPayhereCurrency() != null &&
                notificationDTO.getMd5sig() != null &&
                notificationDTO.getStatusCode() != 0;
    }
}

