package com.curcus.lms.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.curcus.lms.model.entity.Student;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Setter
@Getter
public class CartResponse implements Serializable {

    private Long cartId;
    private Student student;

    @Override
    public String toString() {
        return "CartResponse{" +
                "cartId=" + cartId +
                ", student=" + student + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CartResponse that = (CartResponse) o;
        return Objects.equals(cartId, that.cartId) && Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, student);
    }
}
