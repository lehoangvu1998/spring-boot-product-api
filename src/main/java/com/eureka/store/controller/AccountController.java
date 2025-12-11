package com.eureka.store.controller;

import com.eureka.store.dto.AccountDTO;
import com.eureka.store.gateway.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @Autowired
    private IAccountService accountService;

    @PostMapping("/createAccount")
    public boolean createAccount(@RequestBody AccountDTO accountDTO){
        return accountService.createAccount(accountDTO);
    }
}
