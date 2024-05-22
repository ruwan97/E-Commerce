package com.rc.ecommerce.service;

import com.rc.ecommerce.model.domain.Order;
import com.rc.ecommerce.model.enums.OrderStatus;

public interface OrderService {
    String generateHash(String orderId, double amount, String currency);

    String generateMd5Sig(String merchantId, String orderId, String amount, String currency, String status);

    void saveOrder(Order order);

    void updateOrderAndPaymentDetails(String orderId, String amount, String currency, OrderStatus orderStatus);
}
