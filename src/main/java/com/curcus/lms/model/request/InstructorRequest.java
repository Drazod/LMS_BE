package com.curcus.lms.model.request;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InstructorRequest implements Serializable {
    @NotBlank(message = "Tên bị thiếu")
    private String name;
    @Email(message = "Email is not valid")
    @NotBlank(message = "email bị thiếu")
    private String email;
    @NotBlank(message = "Mật khẩu bị thiếu")
    private String password;
    private String firstName;
    private String lastName;
    @NotBlank(message = "Số điện thoại bị thiếu")
    private String phoneNumber;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    
}
