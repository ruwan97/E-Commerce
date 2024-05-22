package com.rc.ecommerce.service.impl;

import com.rc.ecommerce.model.domain.*;
import com.rc.ecommerce.model.enums.OrderStatus;
import com.rc.ecommerce.model.enums.PaymentStatus;
import com.rc.ecommerce.repository.*;
import com.rc.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payHere.merchantId}")
    private String MERCHANT_ID;

    @Value("${payHere.merchantSecret}")
    private String MERCHANT_SECRET;

    @Override
    public String generateHash(String orderId, double amount, String currency) {
        String hashedSecret = getMd5(MERCHANT_SECRET).toUpperCase();
        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(amount);
        String data = MERCHANT_ID + orderId + amountFormatted + currency + hashedSecret;
        return getMd5(data).toUpperCase();
    }

    @Override
    public String generateMd5Sig(String merchantId, String orderId, String amount, String currency, String statusCode) {
        String hashedSecret = getMd5(MERCHANT_SECRET).toUpperCase();
        String data = merchantId + orderId + amount + currency + statusCode + hashedSecret;
        return getMd5(data).toUpperCase();
    }

    private String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderAndPaymentDetails(String orderId, String amount, String currency, OrderStatus orderStatus) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order != null) {
            order.setStatus(orderStatus);
            order.setUpdatedAt(new Date());
            Payment payment = order.getPayment();
            if (payment != null) {
                payment.setPaymentStatus(PaymentStatus.PAID);
                payment.setPaymentAmount(new BigDecimal(amount));
                payment.setCurrency(currency);
                payment.setPaidAt(new Date());
            }
            orderRepository.save(order);
        }
    }
}


