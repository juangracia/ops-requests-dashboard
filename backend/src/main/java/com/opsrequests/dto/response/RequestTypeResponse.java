package com.opsrequests.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTypeResponse {

    private Long id;
    private String code;
    private String name;
    private Boolean active;
}
