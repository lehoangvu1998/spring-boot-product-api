package com.eureka.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table (name = "vehicles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "licensePlateNumber")
    private String licensePlateNumber;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private int year;

    @Column(name = "vin")
    private String vin;

    @Column(name = "color")
    private String color;

    @Column(name = "effectiveDate")
    private Timestamp effectiveDate;

    @Column(name = "expiredDate")
    private Timestamp expiredDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "status")
    private String status;

    @Column(name = "createdDate")
    private Timestamp createdDate;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "modifiedBy")
    private String modifiedBy;

    @Column(name = "dateModified")
    private Timestamp dateModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

}
