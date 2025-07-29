package com.eureka.store.gateway;

import com.eureka.store.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IProductRepositoryCustom {
    List<Product> getAllProduct();
    Product getProductById(Long id);
    List<Product> createProducts(List <Product> products);
    void updateProducts(Product products);
}
