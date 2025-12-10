package com.eureka.store.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private Integer accountId;
    private BigDecimal balance;
    private String status;
    private Timestamp createdDate;
    private String createdBy;
    private String modifiedBy;
    private Timestamp dateModified;
    private List<PersonDTO> users;
    private List<VehicleDTO> vehicles;
}
