package com.goodfin.backend.controller;

import com.goodfin.backend.dto.auth.LoginAPIResponse;
import com.goodfin.backend.dto.auth.LoginUserDto;
import com.goodfin.backend.dto.auth.RegisterUserDto;
import com.goodfin.backend.mapper.user.UserMapper;
import com.goodfin.backend.model.User;
import com.goodfin.backend.service.AuthenticationService;
import com.goodfin.backend.service.JwtService;
import com.goodfin.backend.service.UserService;
import com.goodfin.backend.utils.ErrorMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequestMapping("/api/v1/auth")
@RestController
@Validated
public class AuthenticationController {
    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private UserMapper userMapper;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        Optional<User> user = userService.findUserByEmail(registerUserDto.getEmail());

        if (user.isPresent()) {
            HashMap<String, HashMap<String, List>> resp = new HashMap<>() {{
                put("errors", new HashMap<>() {{
                    put("email", List.of("Email already exist"));
                }});
            }};

            return new ResponseEntity<>(resp, HttpStatus.CONFLICT);
        } else {
            user = userService.findUserByMobile(registerUserDto.getMobile());

            if (user.isPresent()) {
                HashMap<String, HashMap<String, List>> resp = new HashMap<>() {{
                    put("errors", new HashMap<>() {{
                        put("mobile", List.of("Mobile already exist"));
                    }});
                }};

                return new ResponseEntity<>(resp, HttpStatus.CONFLICT);
            }
        }

        try {
            user = Optional.ofNullable(authenticationService.register(registerUserDto));

            if (user.isPresent()) {
                return new ResponseEntity<>(userMapper.mapUserToRegisterAPIResponseDto(user.get()), HttpStatus.CREATED);
            }

            return new ResponseEntity<>(new HashMap<>(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(errorMapper.createErrorMap(e), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        if (authenticatedUser == null) {
            HashMap<String, HashMap<String, List>> resp = new HashMap<>() {{
                put("errors", new HashMap<>() {{
                    put("email", List.of("Bad credentials."));
                }});
            }};

            return new ResponseEntity<>(resp, HttpStatus.CONFLICT);
        }

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginAPIResponse loginResponse = new LoginAPIResponse();
        loginResponse.setAccess(jwtToken);
        loginResponse.setRefresh(jwtToken);
        loginResponse.setUser(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
