package com.eureka.store.repository;

import com.eureka.store.entity.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOutBoxEventRepository  extends JpaRepository <OutBoxEvent, Long> {
    @Query(value = """
    SELECT * FROM outbox_event
    WHERE status IN ('PENDING', 'FAILED')
      AND (next_retry_at IS NULL OR next_retry_at <= NOW())
    ORDER BY id
    LIMIT :limit
    FOR UPDATE SKIP LOCKED
""", nativeQuery = true)
    List<OutBoxEvent> lockBatchForProcessing(int limit);
}
