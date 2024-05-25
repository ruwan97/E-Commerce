package com.rc.ecommerce.service.impl;

import com.rc.ecommerce.service.PayHereService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

@Service
public class PayHereServiceImpl implements PayHereService {
    private static final Logger logger = LoggerFactory.getLogger(PayHereServiceImpl.class);

    @Value("${payHere.merchant.id}")
    private String merchantId;

    @Value("${payHere.merchant.secret}")
    private String merchantSecret;

    @Override
    public String generateHash(String orderId, double amount, String currency) {
        String hashedSecret = getMd5(merchantSecret).toUpperCase();
        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(amount);
        String data = merchantId + orderId + amountFormatted + currency + hashedSecret;
        return getMd5(data).toUpperCase();
    }

    @Override
    public String generateMd5Sig(String merchantId, String orderId, String amount, String currency, int statusCode) {
        String hashedSecret = getMd5(merchantSecret).toUpperCase();
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
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
