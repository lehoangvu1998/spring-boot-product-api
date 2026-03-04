package com.eureka.store.gateway;

import com.eureka.store.dto.AccountDTO;
import com.eureka.store.entity.Account;

public interface IAccountService {
    AccountDTO createAccount(AccountDTO account);
}
