package com.curcus.lms.model.request;

import java.io.Serializable;
import java.util.Set;

import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.entity.Enrollment;
import com.curcus.lms.model.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRequest implements Serializable {

    private Long courseId;

    private String title;

    private String description;

    private Long price;

    private Long instructorId;

    private Long categoryId;

}
