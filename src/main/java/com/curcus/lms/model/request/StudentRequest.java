package com.curcus.lms.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StudentRequest {

    @NotBlank(message = "Please enter all required fields")
    private String name;

    @NotBlank(message = "Please enter all required fields")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Please enter all required fields")
    private String password;

    private String firstName;
    private String lastName;

    @NotBlank(message = "Please enter all required fields")
    private String phoneNumber;
    private String publicAvtId;
    private String avt;
}