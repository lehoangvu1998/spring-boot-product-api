package com.eureka.store.service;

import com.eureka.store.dto.ProductDTO;
import com.eureka.store.entity.Product;
import com.eureka.store.exception.ProductNotFoundException;
import com.eureka.store.gateway.IProductRepositoryCustom;
import com.eureka.store.gateway.IProductServices;
import com.eureka.store.repository.IProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductServices {

    IProductRepositoryCustom productRepository;

    IProductRepository iProductRepository;

    // Khai báo logger cho lớp ProductService
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductService(IProductRepositoryCustom productRepository, IProductRepository iProductRepository) {}

    @Override
    public List<ProductDTO> getAllProduct() {
        try {

        }catch(Exception ignored){

        }
        return null;
    }

    @Override
    public ProductDTO getProductById(Long id) {
        logger.info("Attempting to retrieve product with ID: {}", id);

        // 1. Kiểm tra ID đầu vào rõ ràng
        if (id == null || id <= 0) {
            logger.warn("Invalid product ID provided: {}", id);
            // Ném ngoại lệ để controller biết lỗi đầu vào
            throw new IllegalArgumentException("Product ID must be a positive non-null value.");
        }

        try {
            // 2. Sử dụng Optional để xử lý trường hợp không tìm thấy
            Optional<Product> productOptional = Optional.ofNullable(productRepository.getProductById(id));

            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                ProductDTO productDTO = convertEntityToDTO(product);
                logger.info("Successfully retrieved and converted product with ID: {}", id);
                return productDTO;
            } else {
                logger.warn("Product with ID {} not found.", id);
                // Ném ngoại lệ nghiệp vụ khi không tìm thấy
                throw new ProductNotFoundException("Product with ID " + id + " not found.");
            }
        } catch (ProductNotFoundException e) {
            // Re-throw để controller xử lý lỗi nghiệp vụ
            throw e;
        } catch (Exception e) { // Bắt các lỗi hệ thống khác (ví dụ: lỗi DB connection)
            logger.error("Error retrieving product with ID {}: {}", id, e.getMessage(), e);
            // Ném một ngoại lệ RuntimeException chung hoặc cụ thể hơn để tầng trên xử lý
            throw new RuntimeException("Failed to retrieve product due to an internal error.", e);
        }
    }

    @Override
    public boolean createProducts(List<ProductDTO> products) {
        logger.info("Attempting to create product with IDs: {}", products);
        boolean isSucess = false;
        List<Product>  productList = new ArrayList<>();

        try {
            for(ProductDTO item : products) {
                if (item.getId() == null || item.getId() <= 0) {
                    logger.warn("Invalid product ID provided: {}", item.getId());
                } else {
                    productList.add(convertDTOToEntity(item));
                }
            }

            iProductRepository.saveAllAndFlush(productList);
            isSucess = true;
        } catch (Exception throwables) {
            logger.error("Error creating product with IDs: {}", products, throwables);
        }
        return isSucess;
    }

    @Override
    public boolean updateProducts(List<ProductDTO> products) {
        logger.info("Attempting to update product with IDs: {}", products);
        boolean isSucess = false;
        List<Product>  productList = new ArrayList<>();

        try {
            for(ProductDTO item : products) {
                if (item.getId() == null || item.getId() <= 0) {
                    logger.warn("Invalid product ID provided: {}", item.getId());
                } else {
                    productList.add(convertDTOToEntity(item));
                }
            }

            iProductRepository.saveAllAndFlush(productList);
            isSucess = true;
        } catch (Exception throwables) {
            logger.error("Error updating product with IDs: {}", products, throwables);
        }
        return isSucess;
    }

    @Transactional
    @Override
    public void deleteProductById(Long id) {
        logger.info("Attempting to delete product with ID: {}", id);
        iProductRepository.deleteProductById(id);
    }

    @Transactional
    @Override
    public void deleteProduct(List<ProductDTO> products) {
        logger.info("Attempting to delete product with IDs: {}", products);
        try{
            for (ProductDTO product : products) {
                iProductRepository.deleteProductById(product.getId());
            }
        }catch (Exception e){
            logger.error("Error trying to delete product with IDs: {}", products, e);
        }
    }

    private ProductDTO convertEntityToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageData(product.getImageData());
        productDTO.setImageName(product.getImageName());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setContentType(product.getContentType());
        return productDTO;
    }

    private Product convertDTOToEntity(ProductDTO product) {
        Product item = new Product();
        item.setId(product.getId());
        item.setName(product.getName());
        item.setPrice(product.getPrice());
        item.setDescription(product.getDescription());
        item.setImageData(product.getImageData());
        item.setImageName(product.getImageName());
        item.setQuantity(product.getQuantity());
        item.setContentType(product.getContentType());
        return item;
    }

    private List<Product> convertToEntity(List<ProductDTO>  products) {
        List<Product> productList = new ArrayList<>();
        for (ProductDTO productDTO : products) {
            productList.add(convertDTOToEntity(productDTO));
        }
        return productList;
    }

}
