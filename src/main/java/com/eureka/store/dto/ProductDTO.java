package com.eureka.store.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String imageName;
    private byte[] imageData; // Hoặc private Blob imageData;
    private String contentType; // Ví dụ: image/jpeg, image/png
}
