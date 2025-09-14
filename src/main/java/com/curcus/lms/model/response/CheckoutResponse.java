package com.curcus.lms.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutResponse {
    private long totalPrice;
    private long discountPrice;
    private long finalPrice;
}
