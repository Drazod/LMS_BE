package com.curcus.lms.model.request;

import com.curcus.lms.model.response.CheckoutResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
public class PurchaseOrderDTO {
    @NotNull(message = "Price is mandatory")
    CheckoutResponse prices;
    @NotNull(message = "IdUser is mandatory")
    long idUser;
    @NotNull(message = "checkoutReq is mandatory")
    CheckoutReq checkoutReq;
}
