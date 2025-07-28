package com.nakqeeb.ecommerce.service;

import com.nakqeeb.ecommerce.dao.UserRepository;
import com.nakqeeb.ecommerce.dto.LoginUserDto;
import com.nakqeeb.ecommerce.dto.RegisterUserDto;
import com.nakqeeb.ecommerce.entity.User;
import com.nakqeeb.ecommerce.exception.InvalidCredentialException;
import com.nakqeeb.ecommerce.exception.UserAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) throws UserAlreadyExistsException {

        Optional<User> existUser = this.userRepository.findByEmail(input.getEmail());
        if (existUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + existUser.get().getEmail() + " already exists.");
        }
        var user = new User();
                user.setFirstName(input.getFirstName());
                user.setLastName(input.getLastName());
                user.setEmail(input.getEmail());
                user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) throws Exception {

        Optional<User> user = this.userRepository.findByEmail(input.getEmail());

        if (user.isEmpty()) {
            throw new InvalidCredentialException("Invalid Credentials");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return this.userRepository.findByEmail(input.getEmail()).orElseThrow();
    }
}