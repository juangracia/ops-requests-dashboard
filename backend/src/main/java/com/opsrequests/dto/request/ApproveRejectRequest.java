package com.opsrequests.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApproveRejectRequest {

    @NotBlank(message = "Comment is required")
    private String comment;
}
