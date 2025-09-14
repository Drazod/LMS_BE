package com.curcus.lms.model.response;

import lombok.Data;

@Data
public class SectionCompleteResponse {
    private Long sectionId;
    private Long position;
    private Boolean courseCompleted;
}
