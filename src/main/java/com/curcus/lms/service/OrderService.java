package com.curcus.lms.service;

import java.util.Map;

import com.curcus.lms.model.request.CheckoutReq;
import com.curcus.lms.model.request.PurchaseOrderDTO;
import com.curcus.lms.model.response.CheckoutResponse;
import com.curcus.lms.model.response.PaymentResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {

    CheckoutResponse checkoutOrder(CheckoutReq checkoutReq);

    PaymentResponse.VNPayResponse processingPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO,
            HttpServletRequest request);

    void completeOrder(Map<String, String> reqParams);
}
