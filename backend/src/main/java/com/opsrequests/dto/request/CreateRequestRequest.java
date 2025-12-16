package com.opsrequests.dto.request;

import com.opsrequests.entity.Request.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateRequestRequest {

    @NotNull(message = "Type ID is required")
    private Long typeId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private BigDecimal amount;

    @NotNull(message = "Priority is required")
    private Priority priority;
}
