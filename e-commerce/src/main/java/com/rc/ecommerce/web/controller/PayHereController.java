package com.rc.ecommerce.web.controller;


import com.rc.ecommerce.model.dto.PaymentDTO;
import com.rc.ecommerce.service.PayHereService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payHere")
public class PayHereController {

    private final PayHereService payHereService;

    @Value("${payHere.preApprove.url}")
    private String preApproveUrl;

    @PostMapping("/preApprove")
    public RedirectView preApprove(@RequestBody PaymentDTO paymentDTO) {
        // generate hash
        String hash = payHereService.generateHash(paymentDTO.getOrderId(), paymentDTO.getAmount(), paymentDTO.getCurrency());

        // set hash in paymentDTO
        paymentDTO.setHash(hash);

        // redirect to PayHere preapproval URL with paymentDTO parameters
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(preApproveUrl);
        redirectView.addStaticAttribute("paymentDTO", paymentDTO);
        return redirectView;
    }
}
