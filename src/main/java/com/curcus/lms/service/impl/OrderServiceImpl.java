package com.curcus.lms.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.curcus.lms.config.VNPayConfig;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.model.entity.Cart;
import com.curcus.lms.model.entity.CartItems;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.Discount;
import com.curcus.lms.model.entity.Student;
import com.curcus.lms.model.entity.User;
import com.curcus.lms.model.request.CheckoutReq;
import com.curcus.lms.model.request.PurchaseOrderDTO;
import com.curcus.lms.model.response.CheckoutResponse;
import com.curcus.lms.model.response.PaymentResponse;
import com.curcus.lms.repository.CartItemsRepository;
import com.curcus.lms.repository.CartRepository;
import com.curcus.lms.repository.CourseRepository;
import com.curcus.lms.repository.DiscountRepository;
import com.curcus.lms.repository.StudentRepository;
import com.curcus.lms.repository.UserRepository;
import com.curcus.lms.service.CartService;
import com.curcus.lms.service.CourseService;
import com.curcus.lms.service.DiscountService;
import com.curcus.lms.service.OrderService;
import com.curcus.lms.service.PaymentService;
import com.curcus.lms.util.VNPayUtil;
import com.curcus.lms.exception.ValidationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final CourseRepository courseRepository;
    private final PaymentService paymentService;
    private final VNPayConfig vnPayConfig;
    private final StudentRepository studentRepository;
    private final CartRepository cartRepository;
    private final DiscountService discountService;
    private final DiscountRepository discountRepository;
    private final CartService cartService;
    private final CartItemsRepository cartItemsRepository;

    @Override
    public CheckoutResponse checkoutOrder(CheckoutReq checkoutReq) {
        long totalPrice = 0;
        for (long idCourse : checkoutReq.getIdCourses()) {
            Course course = courseRepository.findById(idCourse).orElseThrow(() -> new NotFoundException(
                    "please update cart"));
            totalPrice += course.getPrice();
        }
        Long idDiscount = checkoutReq.getIdDiscount();
        long discountPrice = 0;

        if (idDiscount != null) {
            Discount discount = discountRepository.findById(idDiscount).orElseThrow(() -> new NotFoundException(
                    "discount don't exist"));
            discountPrice = discount.getValue();
        }
        return CheckoutResponse.builder().totalPrice(totalPrice).discountPrice(discountPrice)
                .finalPrice(totalPrice - discountPrice).build();
    }

    @Override
    public PaymentResponse.VNPayResponse processingPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO,
            HttpServletRequest request) {
        long idUser = purchaseOrderDTO.getIdUser();
        long idCart = purchaseOrderDTO.getCheckoutReq().getIdCart();
        StringBuilder orderInfo = new StringBuilder();
        orderInfo.append(idUser + "##");
        for (Long idCourse : purchaseOrderDTO.getCheckoutReq().getIdCourses()) {
            cartItemsRepository.findByIdCardAndIdCourse(idCart, idCourse).orElseThrow(() -> new NotFoundException(
                    "product don't exist in cart"));
            orderInfo.append(idCourse + "#");
        }
        Long idDiscount = purchaseOrderDTO.getCheckoutReq().getIdDiscount();
        if (idDiscount != null) {
            orderInfo.append("#" + purchaseOrderDTO.getCheckoutReq().getIdDiscount());
        }
        CheckoutResponse checkoutResponse = checkoutOrder(purchaseOrderDTO.getCheckoutReq());
        if (checkoutResponse.getTotalPrice() != purchaseOrderDTO.getPrices().getTotalPrice()
                || checkoutResponse.getFinalPrice() != purchaseOrderDTO.getPrices().getFinalPrice()
                || checkoutResponse.getDiscountPrice() != purchaseOrderDTO.getPrices().getDiscountPrice()) {
            throw new NotFoundException("error about money");
        }
        return paymentService.createVnPayPayment(request, checkoutResponse.getFinalPrice(), orderInfo.toString());
    }

    @Override
    public void completeOrder(Map<String, String> reqParams) {
        String vnp_SecureHash = reqParams.remove("vnp_SecureHash");

        if (vnp_SecureHash == null) {
            throw new NotFoundException("vnp_SecureHash is required");
        }
        String hashData = VNPayUtil.getPaymentURL(reqParams, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        if (!vnpSecureHash.equals(vnp_SecureHash)) {
            throw new ValidationException("vnp_SecureHash is invalid");
        }
        String total_price = reqParams.remove("vnp_Amount");
        System.err.println(reqParams.get("vnp_OrderInfo"));
        String[] infoOrder = reqParams.get("vnp_OrderInfo").split("##");
        long idUser = Long.parseLong(infoOrder[0]);

        List<Long> idCourses = Arrays.asList(infoOrder[1].split("#")).stream().map(Long::parseLong)
                .collect(Collectors.toList());
        cartService.copyCartToOrder(idUser, idCourses, Long.parseLong(total_price) / 1000);
        System.err.println(infoOrder.length);
        if (infoOrder.length == 3) {
            System.err.println(infoOrder[2]);

            long idDiscount = Long.parseLong(infoOrder[2]);
            System.err.println(idDiscount);
            discountService.deleteDiscountFromStudent(idDiscount, idUser);
        }
        // call deleteDiscount when payment success
    }

}
