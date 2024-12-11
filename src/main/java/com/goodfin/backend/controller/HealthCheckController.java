package com.goodfin.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RequestMapping("/api/v1")
@RestController
public class HealthCheckController {
    @GetMapping("/health-check")
    public ResponseEntity<?> healthCheck() {
        HashMap<String, String> resp = new HashMap<>();
        resp.put("health", "green");

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
