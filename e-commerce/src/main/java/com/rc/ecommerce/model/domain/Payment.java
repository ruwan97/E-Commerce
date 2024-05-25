package com.rc.ecommerce.model.domain;

import com.rc.ecommerce.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    private String paymentId;
    private int statusCode;
    private String method;
    private String statusMessage;
    private String cardHolderName;
    private String cardNo;
    private String cardExpiry;
    private String chargeId;
    private Date paidAt;
    private String currency;
    private BigDecimal paymentAmount;
    private String customerToken;
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
