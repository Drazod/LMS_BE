package com.curcus.lms.model.response;

import com.curcus.lms.constants.SessionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionCreateResponse {
    private Long sectionId;
    private String sectionName;
    private String title;
    private String description;
    private SessionType sessionType;
    private Long position;
    private List<ContentResponse> contents;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ContentResponse {
        private Long contentId;
        private String contentType;
        private String content;
        private Long position;
    }
}