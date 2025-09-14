package com.curcus.lms.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegisterRequest {
    @NotEmpty(message = "Role is mandatory")
    private String userRole;
    @NotEmpty(message = "Name is mandatory")
    private String name;
    @NotEmpty(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
    @NotEmpty(message = "Password is mandatory")
    private String password;
}
