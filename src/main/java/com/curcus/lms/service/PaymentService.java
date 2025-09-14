package com.curcus.lms.service;

import com.curcus.lms.model.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    PaymentResponse.VNPayResponse createVnPayPayment(HttpServletRequest request, long amount, String orderInfo);
}
