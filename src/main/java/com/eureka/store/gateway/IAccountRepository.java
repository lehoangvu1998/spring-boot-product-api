package com.eureka.store.gateway;

import com.eureka.store.entity.Account;

public interface IAccountRepository {
    boolean createAccount(Account account);
}
