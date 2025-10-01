package com.curcus.lms.model.response;

import com.curcus.lms.constants.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response containing details of the created session")
public class SessionCreateResponse {
    @Schema(description = "Unique identifier of the created section", example = "123")
    private Long sectionId;
    
    @Schema(description = "Name of the section/session", example = "Listening Comprehension 1")
    private String sectionName;
    
    @Schema(description = "Title of the session", example = "Introduction to English Listening")
    private String title;
    
    @Schema(description = "Description of the session", example = "Basic listening exercises with audio and visual aids")
    private String description;
    
    @Schema(description = "Type of session", example = "LISTEN")
    private SessionType sessionType;
    
    @Schema(description = "Position of this session within the course", example = "1")
    private Long position;
    
    @Schema(description = "List of content items in this session")
    private List<ContentResponse> contents;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Individual content item within the session")
    public static class ContentResponse {
        @Schema(description = "Unique identifier of the content", example = "456")
        private Long contentId;
        
        @Schema(description = "Type of content", example = "TEXT")
        private String contentType;
        
        @Schema(description = "Content data - text or URL depending on type", example = "Listen to the following audio and answer the questions below.")
        private String content;
        
        @Schema(description = "Position of this content within the session", example = "1")
        private Long position;
    }
}