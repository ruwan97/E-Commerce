package com.rc.ecommerce.model.domain;

import com.rc.ecommerce.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String notifyUrl;
    private String hash;
    private String items;
    private BigDecimal totalAmount;
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    private Date createdAt;
    private Date updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
