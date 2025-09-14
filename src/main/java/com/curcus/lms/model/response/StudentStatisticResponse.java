package com.curcus.lms.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.*;

@AllArgsConstructor
@Setter
@Getter
public class StudentStatisticResponse implements Serializable {

    private Integer purchaseCourse;
    private Integer finishCourse;
    private HashMap<String, Integer> purchaseCourse5;
    private HashMap<String, Integer> finishCourse5;

    @Override
    public String toString() {
        return "StudentStatisticResponse{" +
                "purchaseCourse=" + purchaseCourse +
                ", finishCourse=" + finishCourse + '\'' + 
                ", purchaseCourse5=" + purchaseCourse5 + '\'' + 
                ", finishCourse5=" + finishCourse5 + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StudentStatisticResponse that = (StudentStatisticResponse) o;
        return Objects.equals(purchaseCourse, that.purchaseCourse) && Objects.equals(finishCourse, that.finishCourse)
        && Objects.equals(purchaseCourse5, that.purchaseCourse5) && Objects.equals(finishCourse5, that.finishCourse5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseCourse, finishCourse, purchaseCourse5, finishCourse5);
    }
}
