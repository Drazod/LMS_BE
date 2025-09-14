package com.curcus.lms.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstructorPublicResponse {
    private String avtUrl;
    private String name;

    @Override
    public String toString() {
        return "InstructorResponse{" +
                "avtUrl=" + avtUrl + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        InstructorPublicResponse that = (InstructorPublicResponse) o;
        return Objects.equals(avtUrl, that.avtUrl) &&
                Objects.equals(name, that.name);
    }


    @Override
    public int hashCode() {
        return Objects.hash(avtUrl, name);
    }
}
