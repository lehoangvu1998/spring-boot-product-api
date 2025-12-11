package com.eureka.store.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDTO {

    private Integer personid;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String lastname;
    private String firstName;
    private String middleName;
    private Timestamp createdDate;
    private String createdBy;
    private String modifiedBy;
    private Timestamp dateModified;
    private List<PaymentDTO> payments;
}
