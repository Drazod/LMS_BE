package com.curcus.lms.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

import java.io.Serializable;

@AllArgsConstructor
@Setter
@Getter
public class EnrollmentResponse implements Serializable {
    private Long enrollmentId;
    private Long studentId;
    private CourseEnrollResponse course;
    private Date enrollmentDate;
    private Boolean isComplete;
    private String certificateLink;
    private Date completionDate;

    @Override
    public String toString() {
        return "EnrollmentResponse{" +
                "enrollmentId=" + enrollmentId +
                ", studentId='" + studentId + '\'' +
                ", course='" + course + '\'' +
                ", enrollmentDate='" + enrollmentDate + '\'' +
                ", isComplete='" + isComplete + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EnrollmentResponse that = (EnrollmentResponse) o;
        return Objects.equals(enrollmentId, that.enrollmentId) && Objects.equals(studentId, that.studentId)
                && Objects.equals(course, that.course) && Objects.equals(enrollmentDate, that.enrollmentDate)
                && Objects.equals(isComplete, that.isComplete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId, studentId, course, enrollmentDate, isComplete);
    }
}
