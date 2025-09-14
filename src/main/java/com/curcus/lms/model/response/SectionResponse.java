package com.curcus.lms.model.response;

import lombok.Data;

@Data
public class SectionResponse {
    private Long sectionId;
    private String sectionName;
    private Long position;
}
