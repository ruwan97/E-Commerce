package com.rc.ecommerce.mapper;

import com.rc.ecommerce.model.dto.PaymentNotifyDTO;
import com.rc.ecommerce.model.dto.SavePaymentDto;
import com.rc.ecommerce.web.controller.dto.ChargeRequest;

public class PaymentMapper {
    public static SavePaymentDto toSavePaymentDto(PaymentNotifyDTO request) {
        return SavePaymentDto.builder()
                .orderId(request.getOrderId())
                .paymentId(request.getPaymentId())
                .statusCode(request.getStatusCode())
                .statusMessage(request.getStatusMessage())
                .cardHolderName(request.getCardHolderName())
                .cardNo(request.getCardNo())
                .cardExpiry(request.getCardExpiry())
                .currency(request.getPayHereCurrency())
                .amount(Double.parseDouble(request.getPayHereAmount()))
                .customerToken(request.getCustomerToken())
                .build();
    }
}
