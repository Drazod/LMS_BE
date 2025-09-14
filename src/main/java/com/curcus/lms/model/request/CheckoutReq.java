package com.curcus.lms.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
public class CheckoutReq {
    @NotNull(message = "idCart is mandatory")
    Long idCart;
    @NotNull(message = "idCartItems is mandatory")
    Long[] idCourses;

    Long idDiscount = null;
}
