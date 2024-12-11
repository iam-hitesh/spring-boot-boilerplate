package com.goodfin.backend.dto.auth;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginAPIResponse {
    private String access;

    private String refresh;

    private String user;

    private long expiresIn;
}
