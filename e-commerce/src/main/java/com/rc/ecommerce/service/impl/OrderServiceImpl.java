package com.rc.ecommerce.service.impl;

import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.domain.*;
import com.rc.ecommerce.model.dto.OrderItemDTO;
import com.rc.ecommerce.model.dto.PlaceOrderRequestDTO;
import com.rc.ecommerce.model.enums.OrderStatus;
import com.rc.ecommerce.repository.*;
import com.rc.ecommerce.service.OrderService;
import com.rc.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Value("${payHere.notify.url}")
    private String notifyUrl;

    @Override
    public Order findOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public Order saveOrder(PlaceOrderRequestDTO orderRequestDTO, String hash) throws EComException {
        Order order = Order.builder()
                .orderId(orderRequestDTO.getOrderId())
                .totalAmount(orderRequestDTO.getAmount())
                .shippingAddress(orderRequestDTO.getAddress())
                .status(OrderStatus.PENDING)
                .createdAt(new Date())
                .updatedAt(new Date())
                .notifyUrl(notifyUrl)
                .hash(hash)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderRequestDTO.getItems()) {
            OrderItem orderItem = new OrderItem();
            Optional<Product> product = productService.getProductById(itemDTO.getProductId());
            if (product.isPresent()) {
                orderItem.setProduct(product.get());
                orderItem.setPrice(product.get().getPrice());
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setOrder(order);
                orderItems.add(orderItem);
            } else {
                throw new EComException(HttpStatus.BAD_REQUEST.value(), "Product not found for product id : " + itemDTO.getProductId());
            }
        }

        order.setOrderItems(orderItems);

        order = orderRepository.save(order);
        return order;
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order != null) {
            order.setStatus(orderStatus);
            order.setUpdatedAt(new Date());
            orderRepository.save(order);
            logger.debug("Order " + orderId + " status updated to " + orderStatus);
        } else {
            logger.warn("Order not found: " + orderId);
        }
    }
}


