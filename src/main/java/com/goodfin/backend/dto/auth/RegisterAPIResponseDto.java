package com.goodfin.backend.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RegisterAPIResponseDto {
    public String firstName;

    public String lastName;

    public String email;

    public String mobile;

    public Date createdAt;

    public Date updatedAt;

    public boolean isActive;

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
