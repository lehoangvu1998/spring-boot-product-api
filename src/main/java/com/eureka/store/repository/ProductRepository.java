package com.eureka.store.repository;

import com.eureka.store.entity.Product;
import com.eureka.store.gateway.IProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository implements IProductRepository {
    @Override
    public List<Product> getAllProduct() {
        return List.of();
    }

    @Override
    public Product getProductById(Long id) {
        return null;
    }

    @Override
    public List<Product> createProducts(List<Product> products) {
        return List.of();
    }

    @Override
    public List<Product> updateProducts(List<Product> products) {
        return List.of();
    }

    @Override
    public void deleteProductById(Long id) {

    }

    @Override
    public void deleteProductById(List<Product> products) {

    }
}
