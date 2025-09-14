package com.curcus.lms.model.response;

import lombok.Getter;
import lombok.Setter;

import com.curcus.lms.model.entity.Cart;
import com.curcus.lms.model.entity.CartItemsId;
import com.curcus.lms.model.entity.Course;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class CartItemsResponse implements Serializable {

    private CartItemsId cartItemsId;
    private Cart cart;
    private Course course;

    @Override
    public String toString() {
        return "CartItemsResponse{" +
                "cartItemsId=" + cartItemsId +
                ", cart=" + cart + '\'' +
                ", course=" + course + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CartItemsResponse that = (CartItemsResponse) o;
        return Objects.equals(cartItemsId, that.cartItemsId) && Objects.equals(cart, that.cart)
                && Objects.equals(course, that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartItemsId, cart, course);
    }
}
