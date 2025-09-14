package com.curcus.lms.model.response;

import lombok.Getter;
import lombok.Setter;

import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.Order;
import com.curcus.lms.model.entity.OrderItemsId;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class OrderItemsResponse implements Serializable {

    private OrderItemsId orderItemsId;
    private Order order;
    private Course course;
    private Long price;

    @Override
    public String toString() {
        return "OrderItemsResponse{" +
                "orderItemsId=" + orderItemsId +
                ", order=" + order + '\'' + 
                ", course=" + course + '\'' + 
                ", price=" + price + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            OrderItemsResponse that = (OrderItemsResponse) o;
        return Objects.equals(orderItemsId, that.orderItemsId) && Objects.equals(order, that.order)
                && Objects.equals(course, that.course) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderItemsId, order, course, price);
    }
}
