package com.eureka.store.gateway;

import com.eureka.store.dto.ProductDTO;

import java.util.List;

public interface IProductServices {

    List<ProductDTO> getAllProduct();
    ProductDTO getProductById(Long id);
    boolean createProducts(List <ProductDTO> products);
    boolean updateProducts(List <ProductDTO> products);
    void deleteProductById(Long id);
    void deleteProduct(List <ProductDTO> products);
}
