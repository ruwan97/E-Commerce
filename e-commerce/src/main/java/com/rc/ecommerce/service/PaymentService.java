package com.rc.ecommerce.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.dto.*;

import java.util.Map;

public interface PaymentService {
    String generateHash(String orderId, double amount, String currency);

    String generateMd5Sig(String merchantId, String orderId, String amount, String currency, int status);

    void savePayment(PaymentNotificationDTO notificationDTO, String customerToken) throws EComException;

    JsonNode getPaymentDetails(String orderId) throws EComException;

    RefundResponse refundPayment(RefundRequestDto refundRequest) throws EComException;

    CapturePaymentResponse capturePayment(CapturePaymentRequestDto request) throws EComException;
}
