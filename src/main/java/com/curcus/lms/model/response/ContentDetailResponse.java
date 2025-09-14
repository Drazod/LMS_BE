package com.curcus.lms.model.response;

import com.curcus.lms.constants.ContentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentDetailResponse {
    private ContentType type;
    private String content;
    private Long position;
    private Long id;
}
