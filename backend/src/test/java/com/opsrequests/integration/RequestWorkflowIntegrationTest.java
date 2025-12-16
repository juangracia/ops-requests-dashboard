package com.opsrequests.integration;

import com.opsrequests.dto.request.ApproveRejectRequest;
import com.opsrequests.dto.request.ChangeStatusRequest;
import com.opsrequests.dto.request.CreateRequestRequest;
import com.opsrequests.dto.response.RequestDetailResponse;
import com.opsrequests.dto.response.RequestResponse;
import com.opsrequests.entity.Request.Priority;
import com.opsrequests.entity.Request.Status;
import com.opsrequests.entity.RequestType;
import com.opsrequests.entity.User;
import com.opsrequests.repository.RequestTypeRepository;
import com.opsrequests.repository.UserRepository;
import com.opsrequests.security.UserPrincipal;
import com.opsrequests.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RequestWorkflowIntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestTypeRepository requestTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User admin;
    private User manager;
    private User employee;
    private RequestType requestType;

    private UserPrincipal adminPrincipal;
    private UserPrincipal managerPrincipal;
    private UserPrincipal employeePrincipal;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("password"))
                .role(User.Role.ADMIN)
                .active(true)
                .build();
        admin = userRepository.save(admin);

        manager = User.builder()
                .email("manager@test.com")
                .password(passwordEncoder.encode("password"))
                .role(User.Role.MANAGER)
                .active(true)
                .build();
        manager = userRepository.save(manager);

        employee = User.builder()
                .email("employee@test.com")
                .password(passwordEncoder.encode("password"))
                .role(User.Role.EMPLOYEE)
                .managerId(manager.getId())
                .active(true)
                .build();
        employee = userRepository.save(employee);

        requestType = RequestType.builder()
                .code("TEST")
                .name("Test Request")
                .active(true)
                .build();
        requestType = requestTypeRepository.save(requestType);

        adminPrincipal = new UserPrincipal(admin.getId(), admin.getEmail(), admin.getPassword(),
                admin.getRole(), admin.getManagerId(), admin.getActive());
        managerPrincipal = new UserPrincipal(manager.getId(), manager.getEmail(), manager.getPassword(),
                manager.getRole(), manager.getManagerId(), manager.getActive());
        employeePrincipal = new UserPrincipal(employee.getId(), employee.getEmail(), employee.getPassword(),
                employee.getRole(), employee.getManagerId(), employee.getActive());
    }

    @Test
    void testCompleteRequestWorkflow() {
        CreateRequestRequest createRequest = new CreateRequestRequest();
        createRequest.setTypeId(requestType.getId());
        createRequest.setTitle("Integration Test Request");
        createRequest.setDescription("Testing complete workflow");
        createRequest.setAmount(BigDecimal.valueOf(500));
        createRequest.setPriority(Priority.HIGH);

        RequestResponse createdRequest = requestService.createRequest(createRequest, employeePrincipal);

        assertNotNull(createdRequest);
        assertEquals(Status.SUBMITTED, createdRequest.getStatus());
        assertEquals("Integration Test Request", createdRequest.getTitle());

        ApproveRejectRequest approveRequest = new ApproveRejectRequest();
        approveRequest.setComment("Approved for processing");

        requestService.approveRequest(createdRequest.getId(), approveRequest, managerPrincipal);

        RequestDetailResponse afterApproval = requestService.getRequestDetail(
                createdRequest.getId(), employeePrincipal);
        assertEquals(Status.APPROVED, afterApproval.getStatus());
        assertEquals(1, afterApproval.getComments().size());
        assertTrue(afterApproval.getAuditEvents().size() >= 2);

        ChangeStatusRequest changeToInProgress = new ChangeStatusRequest();
        changeToInProgress.setStatus(Status.IN_PROGRESS);
        changeToInProgress.setNote("Starting work on request");

        requestService.changeStatus(createdRequest.getId(), changeToInProgress, adminPrincipal);

        RequestDetailResponse afterInProgress = requestService.getRequestDetail(
                createdRequest.getId(), employeePrincipal);
        assertEquals(Status.IN_PROGRESS, afterInProgress.getStatus());

        ChangeStatusRequest changeToDone = new ChangeStatusRequest();
        changeToDone.setStatus(Status.DONE);
        changeToDone.setNote("Request completed successfully");

        requestService.changeStatus(createdRequest.getId(), changeToDone, adminPrincipal);

        RequestDetailResponse finalRequest = requestService.getRequestDetail(
                createdRequest.getId(), employeePrincipal);
        assertEquals(Status.DONE, finalRequest.getStatus());
        assertTrue(finalRequest.getAuditEvents().size() >= 4);
    }

    @Test
    void testRequestRejectionWorkflow() {
        CreateRequestRequest createRequest = new CreateRequestRequest();
        createRequest.setTypeId(requestType.getId());
        createRequest.setTitle("Request to be Rejected");
        createRequest.setDescription("This will be rejected");
        createRequest.setPriority(Priority.LOW);

        RequestResponse createdRequest = requestService.createRequest(createRequest, employeePrincipal);

        ApproveRejectRequest rejectRequest = new ApproveRejectRequest();
        rejectRequest.setComment("Insufficient justification");

        requestService.rejectRequest(createdRequest.getId(), rejectRequest, managerPrincipal);

        RequestDetailResponse rejectedRequest = requestService.getRequestDetail(
                createdRequest.getId(), employeePrincipal);
        assertEquals(Status.REJECTED, rejectedRequest.getStatus());
        assertEquals(1, rejectedRequest.getComments().size());
        assertEquals("Insufficient justification", rejectedRequest.getComments().get(0).getComment());
    }
}
