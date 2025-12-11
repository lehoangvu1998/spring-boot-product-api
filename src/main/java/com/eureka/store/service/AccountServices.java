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
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServices {

    private IAccountRepository accountRepository;

    private boolean createAccount(AccountDTO input) {

        //return accountRepository.createAccount(convertDTO(input));
        return true;
    }

    private Account convertDTO(AccountDTO input) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        return null;
    }

}
