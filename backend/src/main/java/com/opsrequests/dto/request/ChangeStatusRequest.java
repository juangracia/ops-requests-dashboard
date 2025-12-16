package com.opsrequests.dto.request;

import com.opsrequests.entity.Request.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusRequest {

    @NotNull(message = "Status is required")
    private Status status;

    private String note;
}
