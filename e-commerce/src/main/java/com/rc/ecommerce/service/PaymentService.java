package com.rc.ecommerce.service;

import com.rc.ecommerce.model.dto.PaymentNotificationDTO;

public interface PaymentService {
    void savePayment(PaymentNotificationDTO notificationDTO, String customerToken);
}
