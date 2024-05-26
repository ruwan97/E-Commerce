package com.rc.ecommerce.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.dto.*;
import com.rc.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${payHere.preApprove.url}")
    private String preApproveUrl;

    @PostMapping("/preApprove")
    public RedirectView preApprove(@RequestBody PaymentDTO paymentDTO) {
        // generate hash
        String hash = paymentService.generateHash(paymentDTO.getOrderId(), paymentDTO.getAmount(), paymentDTO.getCurrency());

        // set hash in paymentDTO
        paymentDTO.setHash(hash);

        // redirect to PayHere preapproval URL with paymentDTO parameters
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(preApproveUrl);
        redirectView.addStaticAttribute("paymentDTO", paymentDTO);
        return redirectView;
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
