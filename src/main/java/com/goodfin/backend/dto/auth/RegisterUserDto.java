package com.goodfin.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
//@NotBlankIfAnotherFieldHasValue(fieldName = "degree", fieldValue = "DOCTORATE", dependFieldName = "url", message = "Url cannot be blank for doctorate degree")
//@NotModifiableDegree(groups = Update.class)
public class RegisterUserDto {
    @NotNull
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 100, message = "Email should be up to 100 characters")
    @Email(message = "Email should be valid", regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,50}")
    private String email;

    @NotNull
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 24, message = "Password should be between 6 to 24 in length.")
    private String password;

    @NotNull
    @NotBlank(message = "Name cannot be blank")
    private String firstName;

    private String lastName;

    @NotNull
    @NotBlank(message = "Mobile cannot be blank")
    @Size(min = 10, max = 14, message = "Mobile should be of 10 digit.")
    private String mobile;
}
