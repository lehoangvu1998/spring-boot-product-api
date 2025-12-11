package com.eureka.store.gateway;

import com.eureka.store.dto.AccountDTO;

public interface IAccountService {
    boolean createAccount(AccountDTO account);
}
