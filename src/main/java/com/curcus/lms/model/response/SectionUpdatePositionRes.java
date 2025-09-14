package com.curcus.lms.model.response;
import java.util.List;
import lombok.Data;
@Data
public class SectionUpdatePositionRes {
    private Long sectionId;
    private String title;
    private Long position;
    private Long courseId;
    private List<Long> contentIds;
}
