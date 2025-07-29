package com.eureka.store.repository;

import com.eureka.store.entity.Product;
import com.eureka.store.gateway.IProductRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductRepository implements IProductRepositoryCustom {

    @PersistenceContext // Tiêm EntityManager để tương tác với DB
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ProductRepository.class);

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
    public void updateProducts(Product products) {
        entityManager.merge(products);
    }

}
