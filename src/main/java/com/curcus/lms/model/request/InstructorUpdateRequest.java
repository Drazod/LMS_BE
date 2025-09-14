package com.curcus.lms.model.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InstructorUpdateRequest {
    @NotBlank(message = "Name is mandatory.")
    private String name;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String publicAvtId;
    private String avt;
}
