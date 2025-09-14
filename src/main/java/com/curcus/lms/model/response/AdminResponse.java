package com.curcus.lms.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse implements Serializable {
    private int adminId;
    private String name;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    @Override
    public String toString() {
        return "StudentResponse{" +
                "studentId=" + adminId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AdminResponse that = (AdminResponse) o;
        return Objects.equals(adminId, that.adminId) && Objects.equals(name, that.name)
                && Objects.equals(email, that.email) && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminId, name, email, firstName, lastName, phoneNumber);
    }
}
