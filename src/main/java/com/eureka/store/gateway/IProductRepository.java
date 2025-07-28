package com.eureka.store.gateway;

import com.eureka.store.entity.Product;

import java.util.List;

public interface IProductRepository {
    List<Product> getAllProduct();
    Product getProductById(Long id);
    List<Product> createProducts(List <Product> products);
    List<Product> updateProducts(List <Product> products);
    void deleteProductById(Long id);
    void deleteProductById(List <Product> products);
}
