package com.eureka.store.dto;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {

    private int vehicleId;
    private String licensePlateNumber;
    private String province;
    private String city;
    private String make;
    private String model;
    private int year;
    private String vin;
    private String color;
    private Timestamp effectiveDate;
    private Timestamp expiredDate;
    private String comment;
    private String status;
    private Timestamp createdDate;
    private String createdBy;
    private String modifiedBy;
    private Timestamp dateModified;
    private AccountDTO account;
}
