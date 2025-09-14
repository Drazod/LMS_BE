package com.curcus.lms.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse implements Serializable {
    private Long userId;
    private String userRole;
    private String name;
    private String email;
    private String avtUrl;
    private String publicAvtId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private AuthenticationResponse tokens;
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
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", avtUrl='" + avtUrl + '\'' +
                ", publicAvtId='" + publicAvtId + '\'' +
                ", tokens=" + tokens + '\'' +
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
        LoginResponse that = (LoginResponse) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(userRole, that.userRole)
                && Objects.equals(name, that.name)
                && Objects.equals(email, that.email)
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(avtUrl, that.avtUrl)
                && Objects.equals(publicAvtId, that.publicAvtId)
                && Objects.equals(tokens, that.tokens)
                && Objects.equals(userAddress, that.userAddress)
                && Objects.equals(userCity, that.userCity)
                && Objects.equals(userCountry, that.userCountry)
                && Objects.equals(userPostalCode, that.userPostalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                userId,
                userRole,
                name,
                email,
                firstName,
                lastName,
                phoneNumber,
                avtUrl,
                publicAvtId,
                tokens,
                userAddress,
                userCity,
                userCountry,
                userPostalCode);
    }
}
