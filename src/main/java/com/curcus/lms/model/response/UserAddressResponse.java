package com.curcus.lms.model.response;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import lombok.Setter;

@Setter
@Getter
public class UserAddressResponse {
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String userAddress;

    private String userCity;

    private String userCountry;

    private Long userPostalCode;
}
