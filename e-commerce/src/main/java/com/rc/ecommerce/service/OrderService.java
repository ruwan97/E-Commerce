package com.rc.ecommerce.service;

import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.domain.Order;
import com.rc.ecommerce.model.dto.PlaceOrderRequestDTO;
import com.rc.ecommerce.model.enums.OrderStatus;

public interface OrderService {
    Order findOrderByOrderId(String orderId);

    Order saveOrder(PlaceOrderRequestDTO orderRequestDTO, String hash) throws EComException;

    void updateOrderStatus(String orderId, OrderStatus orderStatus);

}
