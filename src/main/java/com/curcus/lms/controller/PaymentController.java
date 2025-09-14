package com.curcus.lms.controller;

import com.curcus.lms.model.response.PaymentResponse;
import com.curcus.lms.service.impl.OrderServiceImpl;
import com.curcus.lms.service.impl.PaymentServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    private final PaymentServiceImpl paymentService;
    private final OrderServiceImpl orderService;

    @Value("${frontend_host:http://localhost:5173}")
    private String frontend_host;

    // @GetMapping("/vn-pay")
    // public ResponseEntity<PaymentResponse.VNPayResponse> pay(HttpServletRequest
    // request) {
    // return new
    // ResponseEntity<>(paymentService.createVnPayPayment(request),HttpStatus.OK);
    // }
    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(@RequestParam Map<String, String> reqParams,
            HttpServletResponse response) throws IOException {
        String status = reqParams.get("vnp_ResponseCode");
        if (status.equals("00")) {
            orderService.completeOrder(reqParams);
            response.sendRedirect(frontend_host + "/payment-success");
        } else {
            response.sendRedirect(frontend_host + "/payment-failed");
        }
    }
}