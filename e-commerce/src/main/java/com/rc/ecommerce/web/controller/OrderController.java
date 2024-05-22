package com.rc.ecommerce.web.controller;

import com.rc.ecommerce.model.domain.Order;
import com.rc.ecommerce.model.enums.OrderStatus;
import com.rc.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Value("${payHere.merchantId}")
    private String MERCHANT_ID;

    @RequestMapping("/")
    public String showOrderForm() {
        return "orderForm";
    }

    @PostMapping("/placeOrder")
    public String checkout(
            @RequestParam("order_id") String orderId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") String currency,
            @RequestParam("first_name") String firstName,
            @RequestParam("last_name") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            Model model) {

        String hash = orderService.generateHash(orderId, amount.doubleValue(), currency);
        logger.debug("hash : {}", hash);

        Order order = Order.builder()
                .orderId(orderId)
                .totalAmount(amount)
                .shippingAddress(address)
                .status(OrderStatus.PENDING)
                .createdAt(new Date())
                .updatedAt(new Date())
                .notifyUrl("http://localhost:8080/order/notify")
                .hash(hash)
                .items("Order " + orderId)
                .build();

        orderService.saveOrder(order);

        model.addAttribute("merchant_id", MERCHANT_ID);
        model.addAttribute("return_url", "http://localhost:8080/order/return");
        model.addAttribute("cancel_url", "http://localhost:8080/order/cancel");
        model.addAttribute("notify_url", "http://localhost:8080/payment/notification/notify");
        model.addAttribute("order_id", orderId);
        model.addAttribute("items", order.getItems());
        model.addAttribute("currency", currency);
        model.addAttribute("amount", amount);
        model.addAttribute("first_name", firstName);
        model.addAttribute("last_name", lastName);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
        model.addAttribute("city", city);
        model.addAttribute("country", "Sri Lanka");
        model.addAttribute("hash", hash);

        return "redirect:/order/payHereCheckout";
    }

    @RequestMapping("/payHereCheckout")
    public String payHereCheckout() {
        return "payHereForm";
    }
}
