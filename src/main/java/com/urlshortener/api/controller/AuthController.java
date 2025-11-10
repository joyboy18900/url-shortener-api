package com.urlshortener.api.controller;

import com.urlshortener.api.request.UserRequest;
import com.urlshortener.api.response.LoginResponse;
import com.urlshortener.api.response.UserResponse;
import com.urlshortener.api.service.AuthService;
import com.urlshortener.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}