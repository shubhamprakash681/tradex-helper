package com.tradex.auth.service;

import com.tradex.auth.dto.AuthDtos.UserResponse;
import com.tradex.auth.dto.UserDtos.ChangePasswordRequest;
import com.tradex.auth.dto.UserDtos.UpdateProfileRequest;
import com.tradex.auth.entity.User;
import com.tradex.auth.repository.UserRepository;
import com.tradex.common.security.JwtPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public UserResponse me(JwtPrincipal principal) {
        return authService.toResponse(findUser(principal));
    }

    @Transactional
    public UserResponse updateProfile(JwtPrincipal principal, UpdateProfileRequest request) {
        User user = findUser(principal);
        user.setFullName(request.fullName().trim());
        return authService.toResponse(user);
    }

    @Transactional
    public void changePassword(JwtPrincipal principal, ChangePasswordRequest request) {
        User user = findUser(principal);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }

    private User findUser(JwtPrincipal principal) {
        return userRepository.findById(principal.userId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }
}
