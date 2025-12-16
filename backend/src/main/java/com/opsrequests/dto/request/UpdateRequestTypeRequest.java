package com.opsrequests.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRequestTypeRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private Boolean active;
}
