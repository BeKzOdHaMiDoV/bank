package com.bank.bank.controller;


import com.bank.bank.model.dto.UserResponse;
import com.bank.bank.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/block/{id}")
    public ResponseEntity<UserResponse> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blockUser(id));
    }
}
