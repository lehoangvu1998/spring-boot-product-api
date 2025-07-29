package com.eureka.store.controller;

import com.eureka.store.dto.ProductDTO;
import com.eureka.store.gateway.IProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductControllers {

    IProductServices productServices;

    @Autowired
    public ProductControllers(IProductServices productServices) {}

    @GetMapping("/products/id")
    public ProductDTO getProductById(@RequestParam Long id) {
        return productServices.getProductById(id);
    }

    @GetMapping("/all")
    public List<ProductDTO> getAllProducts() {
        return productServices.getAllProduct();
    }

    @PostMapping("/create")
    public boolean createProduct(@RequestBody List<ProductDTO> products) {
        return productServices.createProducts(products);
    }

    @DeleteMapping("/delete")
    public void deleteMultipleData(@RequestBody List<ProductDTO> products) {
        productServices.deleteProduct(products);
    }

    @DeleteMapping("/delete/id")
    public void deleteMultipleData(@RequestParam Long id) {
        productServices.deleteProductById(id);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody List<ProductDTO> products) {
        return productServices.updateProducts(products);
    }

}
