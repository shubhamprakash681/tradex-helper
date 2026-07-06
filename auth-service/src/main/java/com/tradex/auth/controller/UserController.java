package com.tradex.auth.controller;

import com.tradex.auth.dto.AuthDtos.UserResponse;
import com.tradex.auth.dto.UserDtos.ChangePasswordRequest;
import com.tradex.auth.dto.UserDtos.UpdateProfileRequest;
import com.tradex.auth.service.UserService;
import com.tradex.common.security.JwtPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    UserResponse me(@AuthenticationPrincipal JwtPrincipal principal) {
        return userService.me(principal);
    }

    @PutMapping("/me")
    UserResponse updateProfile(@AuthenticationPrincipal JwtPrincipal principal,
                               @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(principal, request);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void changePassword(@AuthenticationPrincipal JwtPrincipal principal,
                        @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal, request);
    }
}
