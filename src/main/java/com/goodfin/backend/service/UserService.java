package com.goodfin.backend.service;

import com.goodfin.backend.model.User;
import com.goodfin.backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);

    }

    public Optional<User> findUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
}
