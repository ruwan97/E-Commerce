package com.rc.ecommerce.web.controller;

import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.dto.PlaceOrderResponseDTO;
import com.rc.ecommerce.model.domain.Order;
import com.rc.ecommerce.model.dto.PlaceOrderRequestDTO;
import com.rc.ecommerce.model.enums.OrderStatus;
import com.rc.ecommerce.service.OrderService;
import com.rc.ecommerce.service.PayHereService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final PayHereService payHereService;

    @Value("${payHere.merchant.id}")
    private String merchantId;

    @Value("${payHere.notify.url}")
    private String notifyUrl;

    @Value("${payHere.return.url}")
    private String returnUrl;

    @Value("${payHere.cancel.url}")
    private String cancelUrl;

    @Value("${payHere.checkout.url}")
    private String checkoutUrl;

    @RequestMapping("/")
    public String showOrderForm() {
        return "order/orderForm";
    }

    @PostMapping("/place")
    public RedirectView placeOrder(@RequestBody PlaceOrderRequestDTO request) throws EComException {
        // generate hash
        String hash = payHereService.generateHash(request.getOrderId(), request.getAmount().doubleValue(), request.getCurrency());
        logger.debug("Hash: {}", hash);

        // save order
        Order order = orderService.saveOrder(request, hash);

        // construct parameters
        Map<String, String> params = getParams(request, order, hash);

        // construct redirect URL
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(checkoutUrl);
        params.forEach(builder::queryParam);
        String redirectUrl = builder.build().encode().toUriString();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    private Map<String, String> getParams(PlaceOrderRequestDTO request, Order order, String hash) {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("return_url", returnUrl);
        params.put("cancel_url", cancelUrl);
        params.put("notify_url", notifyUrl);
        params.put("order_id", request.getOrderId());
        params.put("items", order.getItems());
        params.put("currency", request.getCurrency());
        params.put("amount", request.getAmount().toString());
        params.put("first_name", request.getFirstName());
        params.put("last_name", request.getLastName());
        params.put("email", request.getEmail());
        params.put("phone", request.getPhone());
        params.put("address", request.getAddress());
        params.put("city", request.getCity());
        params.put("country", "Sri Lanka");
        params.put("recurrence", request.getRecurrence());
        params.put("duration", request.getDuration());
        params.put("hash", hash);
        return params;
    }

    @GetMapping("/return")
    public String handleReturn(@RequestParam("order_id") String orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.SUCCESS);
        return "redirect:/order/success?order_id=" + orderId;
    }

    @GetMapping("/cancel")
    public String handleCancel(@RequestParam("order_id") String orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        return "redirect:/order/failure?order_id=" + orderId;
    }


}
