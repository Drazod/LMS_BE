package com.curcus.lms.model.request;
import lombok.Data;
@Data
public class SectionUpdatePositionRequest {
    private Long sectionId;
    private Long newPosition;
}
