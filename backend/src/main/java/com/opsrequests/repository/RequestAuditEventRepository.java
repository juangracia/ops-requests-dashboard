package com.opsrequests.repository;

import com.opsrequests.entity.RequestAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestAuditEventRepository extends JpaRepository<RequestAuditEvent, Long> {

    List<RequestAuditEvent> findByRequestIdOrderByCreatedAtAsc(Long requestId);
}
