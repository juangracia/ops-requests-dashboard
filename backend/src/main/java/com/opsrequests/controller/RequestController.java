package com.opsrequests.controller;

import com.opsrequests.dto.request.*;
import com.opsrequests.dto.response.CommentResponse;
import com.opsrequests.dto.response.RequestDetailResponse;
import com.opsrequests.dto.response.RequestResponse;
import com.opsrequests.security.UserPrincipal;
import com.opsrequests.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Tag(name = "Requests", description = "Request management endpoints")
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    @Operation(summary = "List requests", description = "List requests filtered by role")
    public ResponseEntity<List<RequestResponse>> getRequests(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String priority) {
        return ResponseEntity.ok(requestService.getRequests(userPrincipal, status, typeId, priority));
    }

    @PostMapping
    @Operation(summary = "Create request", description = "Create a new request")
    public ResponseEntity<RequestResponse> createRequest(
            @Valid @RequestBody CreateRequestRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(requestService.createRequest(request, userPrincipal));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get request detail", description = "Get request detail with comments and audit events")
    public ResponseEntity<RequestDetailResponse> getRequestDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(requestService.getRequestDetail(id, userPrincipal));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update request", description = "Update request (only owner, only if SUBMITTED)")
    public ResponseEntity<RequestResponse> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequestRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(requestService.updateRequest(id, request, userPrincipal));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel request", description = "Cancel request (only owner, only if SUBMITTED)")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        requestService.cancelRequest(id, userPrincipal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve request", description = "Approve request (manager only, requires comment)")
    public ResponseEntity<Void> approveRequest(
            @PathVariable Long id,
            @Valid @RequestBody ApproveRejectRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        requestService.approveRequest(id, request, userPrincipal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject request", description = "Reject request (manager only, requires comment)")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long id,
            @Valid @RequestBody ApproveRejectRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        requestService.rejectRequest(id, request, userPrincipal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "Change status", description = "Change status (admin only: APPROVED→IN_PROGRESS→DONE)")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        requestService.changeStatus(id, request, userPrincipal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment", description = "Add comment to request")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(requestService.addComment(id, request, userPrincipal));
    }
}
