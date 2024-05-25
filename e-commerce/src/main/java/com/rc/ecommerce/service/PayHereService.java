package com.rc.ecommerce.service;

public interface PayHereService {
    String generateHash(String orderId, double amount, String currency);

    String generateMd5Sig(String merchantId, String orderId, String amount, String currency, int status);

}
