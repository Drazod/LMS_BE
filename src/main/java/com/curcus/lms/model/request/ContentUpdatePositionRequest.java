package com.curcus.lms.model.request;

import lombok.Data;
@Data
public class ContentUpdatePositionRequest {
    private Long contentId;
    private Long newPosition;
}
