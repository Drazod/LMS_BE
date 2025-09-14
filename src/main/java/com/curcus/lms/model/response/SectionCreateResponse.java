package com.curcus.lms.model.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class SectionCreateResponse implements Serializable {
    private Long courseId;
    private String sectionName;
    private Long position;
}
