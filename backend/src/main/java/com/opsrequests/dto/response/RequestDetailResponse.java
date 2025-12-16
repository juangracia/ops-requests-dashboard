package com.opsrequests.dto.response;

import com.opsrequests.entity.Request.Priority;
import com.opsrequests.entity.Request.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetailResponse {

    private Long id;
    private UserResponse requester;
    private UserResponse manager;
    private RequestTypeResponse type;
    private String title;
    private String description;
    private BigDecimal amount;
    private Priority priority;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> comments;
    private List<AuditEventResponse> auditEvents;
}
