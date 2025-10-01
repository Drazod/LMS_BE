package com.curcus.lms.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SectionDetailResponse2 {
    private Long sectionId;
    private String sectionName;
    private String title;
    private Long position;
    private String description;
    private List<ContentDetailResponse> contents;
}

