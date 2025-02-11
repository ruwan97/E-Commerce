package com.rc.ecommerce.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.rc.ecommerce.config.PayHereConfig;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.mapper.PaymentMapper;
import com.rc.ecommerce.model.dto.*;
import com.rc.ecommerce.model.enums.OrderStatus;
import com.rc.ecommerce.model.enums.PaymentStatus;
import com.rc.ecommerce.service.OrderService;
import com.rc.ecommerce.service.PaymentService;
import com.rc.ecommerce.web.controller.dto.ChargeRequest;
import com.rc.ecommerce.web.controller.dto.ChargeResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final PayHereConfig payHereConfig;

    @PostMapping("/notify")
    public ResponseEntity<String> handleNotification(@RequestBody PaymentNotifyDTO notificationDTO) throws EComException {
        // validate request
        if (!isValidNotificationRequest(notificationDTO)) {
            return new ResponseEntity<>("Invalid payment notification request", HttpStatus.BAD_REQUEST);
        }

        String localMd5sig = payHereConfig.generateMd5Sig(notificationDTO.getMerchantId(), notificationDTO.getOrderId(), notificationDTO.getPayHereAmount(), notificationDTO.getPayHereCurrency(), notificationDTO.getStatusCode());
        logger.debug("Received payment notification for order : {}", notificationDTO.getOrderId());
        logger.debug("Received md5sig : {}", notificationDTO.getMd5sig());
        logger.debug("Calculated md5sig : {}", localMd5sig);

        // validate and process payment notification
        if (!localMd5sig.equals(notificationDTO.getMd5sig())) {
            return new ResponseEntity<>("MD5 signature verification failed", HttpStatus.BAD_REQUEST);
        }

        if (PaymentStatus.getById(notificationDTO.getStatusCode()).equals(PaymentStatus.SUCCESS)) {
            logger.info("Payment successful for order : {}", notificationDTO.getOrderId());

            // save payment
            SavePaymentDto savePaymentDto = PaymentMapper.toSavePaymentDto(notificationDTO);
            paymentService.savePayment(savePaymentDto);

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

    private boolean isValidNotificationRequest(PaymentNotifyDTO notificationDTO) {
        if (notificationDTO == null) {
            return false;
        }

        return notificationDTO.getMerchantId() != null &&
                notificationDTO.getOrderId() != null &&
                notificationDTO.getPayHereAmount() != null &&
                notificationDTO.getPayHereCurrency() != null &&
                notificationDTO.getMd5sig() != null &&
                notificationDTO.getStatusCode() != 0;
    }

    @PostMapping("/preApprove")
    public RedirectView preApprove(@RequestBody PaymentDTO paymentDTO) {
        // generate hash
        String hash = payHereConfig.generateHash(paymentDTO.getMerchantId(), paymentDTO.getOrderId(), paymentDTO.getAmount(), paymentDTO.getCurrency());

        // set hash in paymentDTO
        paymentDTO.setHash(hash);

        // redirect to PayHere preapproval URL with paymentDTO parameters
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(payHereConfig.getPreApproveUrl());
        redirectView.addStaticAttribute("paymentDTO", paymentDTO);
        return redirectView;
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> initiateCharge(@RequestBody ChargeRequest request) {
        try {
            ChargeResponse response = paymentService.chargeCustomer(request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ChargeResponse());
        }
    }

    @GetMapping("/payment-details")
    public ResponseEntity<JsonNode> getPaymentDetails(@RequestParam String orderId) {
        try {
            JsonNode paymentDetails = paymentService.getPaymentDetails(orderId);
            return ResponseEntity.ok(paymentDetails);
        } catch (EComException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    @PostMapping("/refund")
    public RefundResponse refundPayment(@RequestBody RefundRequestDto refundRequest) throws EComException {
        return paymentService.refundPayment(refundRequest);
    }

    @PostMapping("/capture")
    public CapturePaymentResponse capturePayment(@RequestBody CapturePaymentRequestDto request) {
        try {
            return paymentService.capturePayment(request);
        } catch (EComException e) {
            throw new RuntimeException(e);
        }
    }
}
