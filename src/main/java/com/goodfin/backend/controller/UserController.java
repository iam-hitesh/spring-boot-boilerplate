package com.goodfin.backend.controller;

import com.goodfin.backend.dto.auth.RegisterAPIResponseDto;
import com.goodfin.backend.mapper.user.UserMapper;
import com.goodfin.backend.model.User;
import com.goodfin.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/self")
    public ResponseEntity<?> authenticatedUser(@AuthenticationPrincipal User user) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(userMapper.mapUserToUserProfileDto(user));
    }

    @GetMapping("/")
    public ResponseEntity<List<RegisterAPIResponseDto>> allUsers() {
        List <User> users = userService.allUsers();

        List<RegisterAPIResponseDto> response = new ArrayList<>();

        for (int i = 0;i < users.size(); ++i) {
            response.add(userMapper.mapUserToRegisterAPIResponseDto(users.get(i)));
        }

        return ResponseEntity.ok(response);
    }
}
