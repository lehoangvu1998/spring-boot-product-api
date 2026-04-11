package com.eureka.store.repository;

import com.eureka.store.entity.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOutBoxEventRepository  extends JpaRepository <OutBoxEvent, Long> {
    List<OutBoxEvent> findTop10ByStatusAndNextRetryAtBefore ( String status, LocalDateTime time);
}
