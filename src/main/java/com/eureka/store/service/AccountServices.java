package com.eureka.store.service;

import com.eureka.store.dto.AccountDTO;
import com.eureka.store.dto.VehicleDTO;
import com.eureka.store.entity.Account;
import com.eureka.store.entity.Payment;
import com.eureka.store.entity.Person;
import com.eureka.store.entity.Vehicle;
import com.eureka.store.gateway.IAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AccountServices {

    private IAccountRepository accountRepository;

    private boolean createAccount(AccountDTO input) {

        //return accountRepository.createAccount(convertDTO(input));
        return true;
    }

    private Account convertDTO(AccountDTO input) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Account account = Account.builder()
                .balance(input.getBalance())
                .status(input.getStatus())
                .createdBy(input.getCreatedBy())
                .modifiedBy(input.getModifiedBy())
                .createdDate(now)
                .dateModified(now)
                .build();

        // ---------------- VEHICLES ----------------
        List<Vehicle> vehicles = input.getVehicles()
                .stream()
                .map(item -> {
                    Vehicle v = Vehicle.builder()
                            .city(item.getCity())
                            .color(item.getColor())
                            .comment(item.getComment())
                            .model(item.getModel())
                            .make(item.getMake())
                            .vin(item.getVin())
                            .dateModified(now)
                            .expiredDate(item.getExpiredDate())
                            .modifiedBy(item.getModifiedBy())
                            .year(item.getYear())
                            .status(item.getStatus())
                            .province(item.getProvince())
                            .effectiveDate(item.getEffectiveDate())
                            .createdBy(item.getCreatedBy())
                            .createdDate(item.getCreatedDate())
                            .licensePlateNumber(item.getLicensePlateNumber())
                            .build();

                    v.setAccount(account);
                    return v;
                })
                .collect(Collectors.toList());

        account.setVehicles(vehicles);

        // ---------------- PERSONS ----------------
        List<Person> persons = input.getPerson()
                .stream()
                .map(item -> {

                    Person person = Person.builder()
                            .email(item.getEmail())
                            .address(item.getAddress())
                            .firstName(item.getFirstName())
                            .lastname(item.getLastname())
                            .middleName(item.getMiddleName())
                            .phone(item.getPhone())
                            .password(item.getPassword())
                            .createdBy(item.getCreatedBy())
                            .modifiedBy(item.getModifiedBy())
                            .createdDate(now)
                            .dateModified(now)
                            .build();

                    person.setAccount(account);

                    // -------- PAYMENTS --------
                    List<Payment> payments = item.getPayments()
                            .stream()
                            .map(i -> {
                                Payment p = Payment.builder()
                                        .amount(i.getAmount())
                                        .methodName(i.getMethodName())
                                        .methodActive(i.isMethodActive())
                                        .cardNumberMasked(i.getCardNumberMasked())
                                        .paypalEmail(i.getPaypalEmail())
                                        .createdBy(i.getCreatedBy())
                                        .createdDate(now)
                                        .dateModified(now)
                                        .modifiedBy(i.getModifiedBy())
                                        .build();

                                p.setPerson(person);
                                return p;
                            })
                            .collect(Collectors.toList());

                    person.setPayment(payments);

                    return person;
                })
                .collect(Collectors.toList());

        account.setPerson(persons);

        return account;
    }


}
