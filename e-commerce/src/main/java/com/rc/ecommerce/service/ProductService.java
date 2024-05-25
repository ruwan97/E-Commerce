package com.rc.ecommerce.service;

import com.rc.ecommerce.model.domain.Product;

import java.util.Optional;

public interface ProductService {
    Optional<Product> getProductById(Integer productId);
}
