package com.rc.ecommerce.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.dto.*;
import com.rc.ecommerce.web.controller.dto.ChargeRequest;
import com.rc.ecommerce.web.controller.dto.ChargeResponse;

public interface PaymentService {
    void savePayment(SavePaymentDto savePaymentDto) throws EComException;

    JsonNode getPaymentDetails(String orderId) throws EComException;

    RefundResponse refundPayment(RefundRequestDto refundRequest) throws EComException;

    CapturePaymentResponse capturePayment(CapturePaymentRequestDto request) throws EComException;

    ChargeResponse chargeCustomer(ChargeRequest request) throws EComException;
}
