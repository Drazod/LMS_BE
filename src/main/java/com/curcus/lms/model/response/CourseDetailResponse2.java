package com.curcus.lms.model.response;

import java.time.LocalDate;
import java.util.List;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailResponse2 {
    private Long courseId;
    private String courseThumbnail;
    private String title;
    private String description;
    private Long price;
    private Long categoryId;
    private List<StudentResponse> studentList;
    private LocalDate createDate;
    private String status = "";
}
