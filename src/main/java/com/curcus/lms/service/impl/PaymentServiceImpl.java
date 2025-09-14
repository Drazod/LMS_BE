package com.curcus.lms.service.impl;

import com.curcus.lms.config.VNPayConfig;
import com.curcus.lms.model.response.PaymentResponse;
import com.curcus.lms.service.PaymentService;
import com.curcus.lms.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final VNPayConfig vnPayConfig;

    public PaymentResponse.VNPayResponse createVnPayPayment(HttpServletRequest request, long amount, String orderInfo) {
        // long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        // String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount * 100L));
        vnpParamsMap.put("vnp_OrderInfo", orderInfo);

        // if (bankCode != null && !bankCode.isEmpty()) {
        // vnpParamsMap.put("vnp_BankCode", bankCode);
        // }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentResponse.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
}