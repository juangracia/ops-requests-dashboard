package com.opsrequests.service;

import com.opsrequests.dto.response.UserResponse;
import com.opsrequests.entity.User;
import com.opsrequests.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
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
}
