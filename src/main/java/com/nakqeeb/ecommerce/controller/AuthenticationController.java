package com.nakqeeb.ecommerce.controller;

import com.nakqeeb.ecommerce.dto.LoginUserDto;
import com.nakqeeb.ecommerce.dto.RegisterUserDto;
import com.nakqeeb.ecommerce.entity.User;
import com.nakqeeb.ecommerce.response.LoginResponse;
import com.nakqeeb.ecommerce.service.AuthenticationService;
import com.nakqeeb.ecommerce.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/auth")
@RestController
@CrossOrigin("http://localhost:4200")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status",  HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse();
                loginResponse.setUserId(authenticatedUser.getId().toString());
                loginResponse.setToken(jwtToken);
                loginResponse.setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status",  HttpStatus.UNAUTHORIZED.value());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

        }

    }
}