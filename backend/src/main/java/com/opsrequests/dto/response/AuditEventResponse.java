package com.opsrequests.dto.response;

import com.opsrequests.entity.RequestAuditEvent.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventResponse {

    private Long id;
    private UserResponse actor;
    private EventType eventType;
    private String fromStatus;
    private String toStatus;
    private String note;
    private LocalDateTime createdAt;
}
