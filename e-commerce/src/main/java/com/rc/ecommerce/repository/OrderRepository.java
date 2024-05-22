package com.rc.ecommerce.repository;

import com.rc.ecommerce.model.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByOrderId(String orderId);
}

