package com.bank.bank.service;


import com.bank.bank.model.dto.UserResponse;
import com.bank.bank.model.entity.User;
import com.bank.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(),
                        u.getUsername(),
                        u.getRole().name(),
                        u.getIsBlocked(),
                        u.getCreatedAt()))
                .toList();
    }

    public UserResponse blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsBlocked(true);
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name(), user.getIsBlocked(), user.getCreatedAt());
    }
}
