package com.eureka.store.repository;

import com.eureka.store.entity.Toll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITollRepository extends JpaRepository<Toll, Long> {
}
