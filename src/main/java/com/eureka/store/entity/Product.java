package com.eureka.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "image_name")
    private String imageName;

    @Lob // Đánh dấu đây là một Large Object
    @Column(name = "image_data", columnDefinition="LONGBLOB") // Hoặc BLOB, VARBINARY(MAX) tùy DB
    private byte[] imageData; // Hoặc private Blob imageData;

    @Column(name = "content_type")
    private String contentType; // Ví dụ: image/jpeg, image/png

}
