package com.curcus.lms.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long userId;
    private String userRole;
    private String name;
    private String email;
    private String avtUrl;
    private String publicAvtId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String userAddress;
    private String userCity;
    private String userCountry;
    private Long userPostalCode;

    @Override
    public String toString() {
        return "StudentResponse{" +
                "studentId=" + userId + '\'' +
                ", userRole='" + userRole + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", avtUrl='" + avtUrl + '\'' +
                ", publicAvtId='" + publicAvtId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userCity='" + userCity + '\'' +
                ", userCountry='" + userCountry + '\'' +
                ", userPostalCode=" + userPostalCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserResponse that = (UserResponse) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(userRole, that.userRole)
                && Objects.equals(name, that.name)
                && Objects.equals(email, that.email)
                && Objects.equals(avtUrl, that.avtUrl)
                && Objects.equals(publicAvtId, that.publicAvtId)
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(userAddress, that.userAddress)
                && Objects.equals(userCity, that.userCity)
                && Objects.equals(userCountry, that.userCountry)
                && Objects.equals(userPostalCode, that.userPostalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId,
                userRole,
                name,
                email,
                avtUrl,
                publicAvtId,
                firstName,
                lastName,
                phoneNumber,
                userAddress,
                userCity,
                userCountry,
                userPostalCode);
    }
}
