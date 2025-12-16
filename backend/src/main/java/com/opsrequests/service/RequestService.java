package com.opsrequests.service;

import com.opsrequests.dto.request.*;
import com.opsrequests.dto.response.*;
import com.opsrequests.entity.*;
import com.opsrequests.entity.Request;
import com.opsrequests.entity.Request.Status;
import com.opsrequests.entity.RequestAuditEvent.EventType;
import com.opsrequests.exception.BadRequestException;
import com.opsrequests.exception.ResourceNotFoundException;
import com.opsrequests.exception.UnauthorizedException;
import com.opsrequests.repository.*;
import com.opsrequests.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final UserRepository userRepository;
    private final RequestCommentRepository commentRepository;
    private final RequestAuditEventRepository auditEventRepository;

    public List<RequestResponse> getRequests(UserPrincipal userPrincipal, String statusFilter,
                                              Long typeIdFilter, String priorityFilter) {
        List<Request> requests;

        switch (userPrincipal.getRole()) {
            case EMPLOYEE:
                requests = requestRepository.findByRequesterId(userPrincipal.getId());
                break;
            case MANAGER:
                if (statusFilter != null && !statusFilter.isEmpty()) {
                    try {
                        Status status = Status.valueOf(statusFilter);
                        requests = requestRepository.findByManagerIdAndStatus(userPrincipal.getId(), status);
                    } catch (IllegalArgumentException e) {
                        requests = requestRepository.findByManagerId(userPrincipal.getId());
                    }
                } else {
                    requests = requestRepository.findByManagerIdAndStatus(
                            userPrincipal.getId(), Status.SUBMITTED);
                }
                break;
            case ADMIN:
                requests = requestRepository.findAll();
                break;
            default:
                requests = List.of();
        }

        return requests.stream()
                .filter(r -> statusFilter == null || statusFilter.isEmpty() ||
                        userPrincipal.getRole() == User.Role.MANAGER ||
                        r.getStatus().name().equals(statusFilter))
                .filter(r -> typeIdFilter == null || r.getType().getId().equals(typeIdFilter))
                .filter(r -> priorityFilter == null || priorityFilter.isEmpty() ||
                        r.getPriority().name().equals(priorityFilter))
                .map(this::mapToRequestResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestResponse createRequest(CreateRequestRequest request, UserPrincipal userPrincipal) {
        RequestType requestType = requestTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Request type not found"));

        User requester = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User manager = null;
        if (requester.getManagerId() != null) {
            manager = userRepository.findById(requester.getManagerId()).orElse(null);
        }

        Request newRequest = Request.builder()
                .requester(requester)
                .manager(manager)
                .type(requestType)
                .title(request.getTitle())
                .description(request.getDescription())
                .amount(request.getAmount())
                .priority(request.getPriority())
                .status(Status.SUBMITTED)
                .build();

        newRequest = requestRepository.save(newRequest);

        createAuditEvent(newRequest, requester, EventType.CREATED, null, Status.SUBMITTED.name(), null);

        return mapToRequestResponse(newRequest);
    }

    public RequestDetailResponse getRequestDetail(Long id, UserPrincipal userPrincipal) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        validateAccess(request, userPrincipal);

        List<CommentResponse> comments = commentRepository.findByRequestIdOrderByCreatedAtAsc(id).stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        List<AuditEventResponse> auditEvents = auditEventRepository.findByRequestIdOrderByCreatedAtAsc(id).stream()
                .map(this::mapToAuditEventResponse)
                .collect(Collectors.toList());

        return RequestDetailResponse.builder()
                .id(request.getId())
                .requester(mapToUserResponse(request.getRequester()))
                .manager(request.getManager() != null ? mapToUserResponse(request.getManager()) : null)
                .type(mapToRequestTypeResponse(request.getType()))
                .title(request.getTitle())
                .description(request.getDescription())
                .amount(request.getAmount())
                .priority(request.getPriority())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .comments(comments)
                .auditEvents(auditEvents)
                .build();
    }

    @Transactional
    public RequestResponse updateRequest(Long id, UpdateRequestRequest request, UserPrincipal userPrincipal) {
        Request existingRequest = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!existingRequest.getRequester().getId().equals(userPrincipal.getId())) {
            throw new UnauthorizedException("You can only update your own requests");
        }

        if (!existingRequest.getStatus().equals(Status.SUBMITTED)) {
            throw new BadRequestException("Can only update requests in SUBMITTED status");
        }

        RequestType requestType = requestTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Request type not found"));

        existingRequest.setType(requestType);
        existingRequest.setTitle(request.getTitle());
        existingRequest.setDescription(request.getDescription());
        existingRequest.setAmount(request.getAmount());
        existingRequest.setPriority(request.getPriority());

        existingRequest = requestRepository.save(existingRequest);

        return mapToRequestResponse(existingRequest);
    }

    @Transactional
    public void cancelRequest(Long id, UserPrincipal userPrincipal) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getRequester().getId().equals(userPrincipal.getId())) {
            throw new UnauthorizedException("You can only cancel your own requests");
        }

        if (!request.getStatus().equals(Status.SUBMITTED)) {
            throw new BadRequestException("Can only cancel requests in SUBMITTED status");
        }

        Status oldStatus = request.getStatus();
        request.setStatus(Status.CANCELLED);
        requestRepository.save(request);

        User actor = userRepository.findById(userPrincipal.getId()).orElseThrow();
        createAuditEvent(request, actor, EventType.CANCELLED, oldStatus.name(), Status.CANCELLED.name(), null);
    }

    @Transactional
    public void approveRequest(Long id, ApproveRejectRequest request, UserPrincipal userPrincipal) {
        Request existingRequest = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        validateManagerAccess(existingRequest, userPrincipal);

        if (!existingRequest.getStatus().equals(Status.SUBMITTED)) {
            throw new BadRequestException("Can only approve requests in SUBMITTED status");
        }

        Status oldStatus = existingRequest.getStatus();
        existingRequest.setStatus(Status.APPROVED);
        requestRepository.save(existingRequest);

        User actor = userRepository.findById(userPrincipal.getId()).orElseThrow();
        createAuditEvent(existingRequest, actor, EventType.APPROVED, oldStatus.name(),
                Status.APPROVED.name(), request.getComment());

        addComment(id, request.getComment(), userPrincipal);
    }

    @Transactional
    public void rejectRequest(Long id, ApproveRejectRequest request, UserPrincipal userPrincipal) {
        Request existingRequest = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        validateManagerAccess(existingRequest, userPrincipal);

        if (!existingRequest.getStatus().equals(Status.SUBMITTED)) {
            throw new BadRequestException("Can only reject requests in SUBMITTED status");
        }

        Status oldStatus = existingRequest.getStatus();
        existingRequest.setStatus(Status.REJECTED);
        requestRepository.save(existingRequest);

        User actor = userRepository.findById(userPrincipal.getId()).orElseThrow();
        createAuditEvent(existingRequest, actor, EventType.REJECTED, oldStatus.name(),
                Status.REJECTED.name(), request.getComment());

        addComment(id, request.getComment(), userPrincipal);
    }

    @Transactional
    public void changeStatus(Long id, ChangeStatusRequest request, UserPrincipal userPrincipal) {
        Request existingRequest = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        validateStatusTransition(existingRequest.getStatus(), request.getStatus());

        Status oldStatus = existingRequest.getStatus();
        existingRequest.setStatus(request.getStatus());
        requestRepository.save(existingRequest);

        User actor = userRepository.findById(userPrincipal.getId()).orElseThrow();
        createAuditEvent(existingRequest, actor, EventType.STATUS_CHANGED, oldStatus.name(),
                request.getStatus().name(), request.getNote());
    }

    @Transactional
    public CommentResponse addComment(Long id, AddCommentRequest request, UserPrincipal userPrincipal) {
        return addComment(id, request.getComment(), userPrincipal);
    }

    private CommentResponse addComment(Long id, String commentText, UserPrincipal userPrincipal) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        validateAccess(request, userPrincipal);

        User author = userRepository.findById(userPrincipal.getId()).orElseThrow();

        RequestComment comment = RequestComment.builder()
                .request(request)
                .author(author)
                .comment(commentText)
                .build();

        comment = commentRepository.save(comment);

        createAuditEvent(request, author, EventType.COMMENT_ADDED, null, null, commentText);

        return mapToCommentResponse(comment);
    }

    private void createAuditEvent(Request request, User actor, EventType eventType,
                                   String fromStatus, String toStatus, String note) {
        RequestAuditEvent event = RequestAuditEvent.builder()
                .request(request)
                .actor(actor)
                .eventType(eventType)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .note(note)
                .build();

        auditEventRepository.save(event);
    }

    private void validateAccess(Request request, UserPrincipal userPrincipal) {
        if (userPrincipal.getRole().equals(User.Role.ADMIN)) {
            return;
        }

        if (userPrincipal.getRole().equals(User.Role.MANAGER)) {
            if (request.getManager() != null &&
                    request.getManager().getId().equals(userPrincipal.getId())) {
                return;
            }
        }

        if (!request.getRequester().getId().equals(userPrincipal.getId())) {
            throw new UnauthorizedException("Access denied");
        }
    }

    private void validateManagerAccess(Request request, UserPrincipal userPrincipal) {
        if (userPrincipal.getRole().equals(User.Role.ADMIN)) {
            return;
        }

        if (!userPrincipal.getRole().equals(User.Role.MANAGER)) {
            throw new UnauthorizedException("Only managers can approve/reject requests");
        }

        if (request.getManager() == null ||
                !request.getManager().getId().equals(userPrincipal.getId())) {
            throw new UnauthorizedException("You can only approve/reject requests assigned to you");
        }
    }

    private void validateStatusTransition(Status currentStatus, Status newStatus) {
        if (currentStatus.equals(Status.APPROVED) && newStatus.equals(Status.IN_PROGRESS)) {
            return;
        }
        if (currentStatus.equals(Status.IN_PROGRESS) && newStatus.equals(Status.DONE)) {
            return;
        }
        throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
    }

    private RequestResponse mapToRequestResponse(Request request) {
        return RequestResponse.builder()
                .id(request.getId())
                .requester(mapToUserResponse(request.getRequester()))
                .manager(request.getManager() != null ? mapToUserResponse(request.getManager()) : null)
                .type(mapToRequestTypeResponse(request.getType()))
                .title(request.getTitle())
                .description(request.getDescription())
                .amount(request.getAmount())
                .priority(request.getPriority())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .managerId(user.getManagerId())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private RequestTypeResponse mapToRequestTypeResponse(RequestType requestType) {
        return RequestTypeResponse.builder()
                .id(requestType.getId())
                .code(requestType.getCode())
                .name(requestType.getName())
                .active(requestType.getActive())
                .build();
    }

    private CommentResponse mapToCommentResponse(RequestComment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .author(mapToUserResponse(comment.getAuthor()))
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private AuditEventResponse mapToAuditEventResponse(RequestAuditEvent event) {
        return AuditEventResponse.builder()
                .id(event.getId())
                .actor(mapToUserResponse(event.getActor()))
                .eventType(event.getEventType())
                .fromStatus(event.getFromStatus())
                .toStatus(event.getToStatus())
                .note(event.getNote())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
