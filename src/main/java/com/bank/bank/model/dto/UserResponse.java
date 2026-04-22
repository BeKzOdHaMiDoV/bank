package com.bank.bank.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private Boolean isBlocked;
    private LocalDateTime createdAt;
}
