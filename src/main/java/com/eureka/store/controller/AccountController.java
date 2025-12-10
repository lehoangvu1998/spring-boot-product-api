package com.eureka.store.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @PostMapping("/createAccount")
    public boolean createAccount(){
        return true;
    }
}
