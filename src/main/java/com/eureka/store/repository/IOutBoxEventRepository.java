package com.eureka.store.repository;

import com.eureka.store.entity.OutBoxEvent;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOutBoxEventRepository  extends JpaRepository <OutBoxEvent, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT e FROM OutBoxEvent e WHERE e.status IN ('PENDING', 'FAILED') " +
            "AND (e.nextRetryAt IS NULL OR e.nextRetryAt <= :now) " +
            "ORDER BY e.id ASC")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "-2")}) // SKIP LOCKED
    List<OutBoxEvent> findBatchForProcessing(@Param("now") LocalDateTime now, Pageable pageable);
    List<OutBoxEvent> lockBatchForProcessing(int limit);
}
