package com.goodfin.backend.mapper.user;

import com.goodfin.backend.dto.auth.RegisterAPIResponseDto;
import com.goodfin.backend.model.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public RegisterAPIResponseDto mapUserToRegisterAPIResponseDto(User user) {
        RegisterAPIResponseDto responseDto = new RegisterAPIResponseDto();

        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        responseDto.setEmail(user.getEmail());
        responseDto.setMobile(user.getMobile());
        responseDto.setCreatedAt(user.getCreatedAt());
        responseDto.setUpdatedAt(user.getUpdatedAt());
        responseDto.setIsActive(user.getIsActive());

        return responseDto;
    }

    public RegisterAPIResponseDto mapUserToUserProfileDto(User user) {
        RegisterAPIResponseDto responseDto = new RegisterAPIResponseDto();

        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        responseDto.setEmail(user.getEmail());
        responseDto.setMobile(user.getMobile());
        responseDto.setCreatedAt(user.getCreatedAt());
        responseDto.setUpdatedAt(user.getUpdatedAt());
        responseDto.setIsActive(user.getIsActive());

        return responseDto;
    }
}
