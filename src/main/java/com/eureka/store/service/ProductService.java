package com.eureka.store.service;

import com.eureka.store.dto.ProductDTO;
import com.eureka.store.entity.Product;
import com.eureka.store.gateway.IProductServices;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductServices {
    @Override
    public List<ProductDTO> getAllProduct() {
        return List.of();
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return null;
    }

    @Override
    public String createProducts(List<ProductDTO> products) {
        return null;
    }

    @Override
    public String updateProducts(List<ProductDTO> products) {
        return null;
    }

    @Override
    public void deleteProductById(Long id) {

    }

    @Override
    public void deleteProduct(List<ProductDTO> products) {

    }
}
