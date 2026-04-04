package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.UpdateRoleRequest;
import com.finance.dashboard.dto.request.UpdateStatusRequest;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Get all users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get only active users
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByActive(true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get single user by id
    public UserResponse getUserById(Long id) {   // ✅ FIX
        User user = findUserById(id);
        return toResponse(user);
    }

    // Update role
    public UserResponse updateRole(Long id, UpdateRoleRequest request) {
        User user = findUserById(id);
        user.setRole(request.getRole());
        userRepository.save(user);
        return toResponse(user);
    }

    // Activate / deactivate
    public UserResponse updateStatus(Long id, UpdateStatusRequest request) {
        User user = findUserById(id);
        user.setActive(request.getActive());
        userRepository.save(user);
        return toResponse(user);
    }

    // Delete user
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    // ---------- helpers ----------

    private User findUserById(Long id) {   // ✅ FIX
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}