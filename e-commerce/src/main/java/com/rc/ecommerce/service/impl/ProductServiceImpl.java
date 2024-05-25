package com.rc.ecommerce.service.impl;

import com.rc.ecommerce.model.domain.Product;
import com.rc.ecommerce.repository.ProductRepository;
import com.rc.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }
}
