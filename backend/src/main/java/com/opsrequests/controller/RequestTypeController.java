package com.opsrequests.controller;

import com.opsrequests.dto.request.CreateRequestTypeRequest;
import com.opsrequests.dto.request.UpdateRequestTypeRequest;
import com.opsrequests.dto.response.RequestTypeResponse;
import com.opsrequests.service.RequestTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-types")
@RequiredArgsConstructor
@Tag(name = "Request Types", description = "Request type management endpoints")
public class RequestTypeController {

    private final RequestTypeService requestTypeService;

    @GetMapping
    @Operation(summary = "List active request types", description = "Get all active request types")
    public ResponseEntity<List<RequestTypeResponse>> getActiveRequestTypes() {
        return ResponseEntity.ok(requestTypeService.getActiveRequestTypes());
    }

    @PostMapping
    @Operation(summary = "Create request type", description = "Create a new request type (admin only)")
    public ResponseEntity<RequestTypeResponse> createRequestType(
            @Valid @RequestBody CreateRequestTypeRequest request) {
        return ResponseEntity.ok(requestTypeService.createRequestType(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update request type", description = "Update request type (admin only)")
    public ResponseEntity<RequestTypeResponse> updateRequestType(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequestTypeRequest request) {
        return ResponseEntity.ok(requestTypeService.updateRequestType(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete request type", description = "Soft delete request type (admin only)")
    public ResponseEntity<Void> deleteRequestType(@PathVariable Long id) {
        requestTypeService.deleteRequestType(id);
        return ResponseEntity.ok().build();
    }
}
