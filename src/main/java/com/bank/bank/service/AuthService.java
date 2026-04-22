package com.bank.bank.service;

import com.bank.bank.model.dto.AuthRequest;
import com.bank.bank.model.dto.RegisterRequest;
import com.bank.bank.model.entity.Account;
import com.bank.bank.model.entity.User;
import com.bank.bank.model.enums.Role;
import com.bank.bank.repository.AccountRepository;
import com.bank.bank.repository.UserRepository;
import com.bank.bank.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isBlocked(false)
                .build();
        userRepository.save(user);

        Account account = Account.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();
        accountRepository.save(account);

        return user;
    }

    public String login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        return jwtUtil.generateToken(request.getUsername());
    }
}
