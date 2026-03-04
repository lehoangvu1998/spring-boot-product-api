package com.eureka.store.repository;

import com.eureka.store.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Integer> {
}
