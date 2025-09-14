package com.curcus.lms.model.response;

import lombok.Getter;
import lombok.Setter;

import com.curcus.lms.model.entity.Student;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Setter
@Getter
public class OrderResponse implements Serializable {

    private Long orderId;
    private Student student;
    private Date paymentDate;
    private Long totalPrice;

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orderId=" + orderId +
                ", student=" + student + '\'' + 
                ", paymentDate=" + paymentDate + '\'' +
                ", totalPrice=" + totalPrice + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            OrderResponse that = (OrderResponse) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(student, that.student)
                && Objects.equals(paymentDate, that.paymentDate) && Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, student, paymentDate, totalPrice);
    }
}
