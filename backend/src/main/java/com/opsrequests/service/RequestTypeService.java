package com.opsrequests.service;

import com.opsrequests.dto.request.CreateRequestTypeRequest;
import com.opsrequests.dto.request.UpdateRequestTypeRequest;
import com.opsrequests.dto.response.RequestTypeResponse;
import com.opsrequests.entity.RequestType;
import com.opsrequests.exception.BadRequestException;
import com.opsrequests.exception.ResourceNotFoundException;
import com.opsrequests.repository.RequestTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestTypeService {

    private final RequestTypeRepository requestTypeRepository;

    public List<RequestTypeResponse> getActiveRequestTypes() {
        return requestTypeRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestTypeResponse createRequestType(CreateRequestTypeRequest request) {
        if (requestTypeRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Request type with code " + request.getCode() + " already exists");
        }

        RequestType requestType = RequestType.builder()
                .code(request.getCode())
                .name(request.getName())
                .active(true)
                .build();

        requestType = requestTypeRepository.save(requestType);
        return mapToResponse(requestType);
    }

    @Transactional
    public RequestTypeResponse updateRequestType(Long id, UpdateRequestTypeRequest request) {
        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request type not found"));

        requestType.setName(request.getName());
        if (request.getActive() != null) {
            requestType.setActive(request.getActive());
        }

        requestType = requestTypeRepository.save(requestType);
        return mapToResponse(requestType);
    }

    @Transactional
    public void deleteRequestType(Long id) {
        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request type not found"));

        requestType.setActive(false);
        requestTypeRepository.save(requestType);
    }

    private RequestTypeResponse mapToResponse(RequestType requestType) {
        return RequestTypeResponse.builder()
                .id(requestType.getId())
                .code(requestType.getCode())
                .name(requestType.getName())
                .active(requestType.getActive())
                .build();
    }
}
