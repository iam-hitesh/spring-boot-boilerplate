package com.goodfin.backend.service;


import com.goodfin.backend.dto.auth.LoginUserDto;
import com.goodfin.backend.dto.auth.RegisterUserDto;
import com.goodfin.backend.model.User;
import com.goodfin.backend.repo.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public User register(@Valid RegisterUserDto input) {
        User user = new User();

        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setMobile(input.getMobile());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                Optional<User> user = userRepository.findByEmail(input.getEmail());

                if (user.isPresent() && user.get().getIsActive() && !user.get().getIsAccountLocked()) {
                    return user.get();
                }
            }

            return null;
        } catch (BadCredentialsException e) {
            return null;
        }
    }
}
