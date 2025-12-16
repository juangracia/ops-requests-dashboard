package com.opsrequests.service;

import com.opsrequests.dto.request.CreateRequestRequest;
import com.opsrequests.dto.response.RequestResponse;
import com.opsrequests.entity.Request;
import com.opsrequests.entity.Request.Priority;
import com.opsrequests.entity.Request.Status;
import com.opsrequests.entity.RequestType;
import com.opsrequests.entity.User;
import com.opsrequests.exception.BadRequestException;
import com.opsrequests.exception.ResourceNotFoundException;
import com.opsrequests.repository.*;
import com.opsrequests.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestCommentRepository commentRepository;

    @Mock
    private RequestAuditEventRepository auditEventRepository;

    @InjectMocks
    private RequestService requestService;

    private User employee;
    private User manager;
    private RequestType requestType;
    private Request request;
    private UserPrincipal employeePrincipal;

    @BeforeEach
    void setUp() {
        manager = User.builder()
                .id(2L)
                .email("manager@example.com")
                .password("password")
                .role(User.Role.MANAGER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        employee = User.builder()
                .id(1L)
                .email("employee@example.com")
                .password("password")
                .role(User.Role.EMPLOYEE)
                .managerId(2L)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        requestType = RequestType.builder()
                .id(1L)
                .code("PURCHASE")
                .name("Purchase Request")
                .active(true)
                .build();

        request = Request.builder()
                .id(1L)
                .requester(employee)
                .manager(manager)
                .type(requestType)
                .title("Test Request")
                .description("Test Description")
                .amount(BigDecimal.valueOf(100))
                .priority(Priority.MEDIUM)
                .status(Status.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        employeePrincipal = new UserPrincipal(1L, "employee@example.com", "password",
                User.Role.EMPLOYEE, 2L, true);
    }

    @Test
    void createRequest_Success() {
        CreateRequestRequest createRequest = new CreateRequestRequest();
        createRequest.setTypeId(1L);
        createRequest.setTitle("Test Request");
        createRequest.setDescription("Test Description");
        createRequest.setAmount(BigDecimal.valueOf(100));
        createRequest.setPriority(Priority.MEDIUM);

        when(requestTypeRepository.findById(1L)).thenReturn(Optional.of(requestType));
        when(userRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(userRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(auditEventRepository.save(any())).thenReturn(null);

        RequestResponse response = requestService.createRequest(createRequest, employeePrincipal);

        assertNotNull(response);
        assertEquals("Test Request", response.getTitle());
        assertEquals(Status.SUBMITTED, response.getStatus());

        verify(requestRepository).save(any(Request.class));
        verify(auditEventRepository).save(any());
    }

    @Test
    void createRequest_InvalidRequestType_ThrowsException() {
        CreateRequestRequest createRequest = new CreateRequestRequest();
        createRequest.setTypeId(999L);
        createRequest.setTitle("Test Request");
        createRequest.setPriority(Priority.MEDIUM);

        when(requestTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> requestService.createRequest(createRequest, employeePrincipal));

        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void getRequests_Employee_ReturnsOwnRequests() {
        when(requestRepository.findByRequesterId(1L)).thenReturn(List.of(request));

        List<RequestResponse> responses = requestService.getRequests(employeePrincipal);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(requestRepository).findByRequesterId(1L);
    }

    @Test
    void cancelRequest_NotSubmitted_ThrowsException() {
        request.setStatus(Status.APPROVED);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(BadRequestException.class,
                () -> requestService.cancelRequest(1L, employeePrincipal));

        verify(requestRepository, never()).save(any(Request.class));
    }
}
