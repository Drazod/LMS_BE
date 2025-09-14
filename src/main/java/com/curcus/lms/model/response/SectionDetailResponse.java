package com.curcus.lms.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SectionDetailResponse {
    private Long sectionId;
    private String sectionName;
    private Long position;
    // private List<ContentDetailResponse> contents;
}
