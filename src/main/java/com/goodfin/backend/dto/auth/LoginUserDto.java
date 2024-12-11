package com.goodfin.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    @NonNull
    @NotBlank
    private String email;

    @NonNull
    @NotBlank
    private String password;
}