package com.curcus.lms.model.request;

import com.curcus.lms.constants.ContentType;
import com.curcus.lms.constants.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(
    description = "Request to create a new session with text-based content only",
    example = """
    {
      "courseId": 1,
      "sectionName": "Listening Comprehension 1",
      "title": "Introduction to English Listening",
      "description": "Basic listening exercises to improve comprehension skills",
      "sessionType": "LISTEN",
      "contents": [
        {
          "contentType": "TEXT",
          "content": "Listen to the following audio and answer the questions below."
        },
        {
          "contentType": "TEXT",
          "content": "What did the speaker mention about the weather?"
        }
      ]
    }
    """
)
public class SessionCreateRequest {
    @NotNull(message = "Course ID cannot be null")
    @Schema(description = "ID of the course to add the session to", example = "1", required = true)
    private Long courseId;
    
    @NotEmpty(message = "Section name cannot be empty")
    @Schema(description = "Name of the section/session", example = "Listening Comprehension 1", required = true)
    private String sectionName;
    
    @NotEmpty(message = "Title cannot be empty")
    @Schema(description = "Title of the session", example = "Introduction to English Listening", required = true)
    private String title;
    
    @Schema(description = "Detailed description of the session", example = "Basic listening exercises to improve comprehension skills")
    private String description;
    
    @NotNull(message = "Session type cannot be null")
    @Schema(description = "Type of session", example = "LISTEN", allowableValues = {"LISTEN", "READING", "SPEAKING"}, required = true)
    private SessionType sessionType;
    
    @Valid
    @Schema(description = "List of content items for this session")
    private List<SessionContentRequest> contents;
    
    @Getter
    @Setter
    @Schema(description = "Individual content item within a session")
    public static class SessionContentRequest {
        @NotNull(message = "Content type cannot be null")
        @Schema(description = "Type of content", example = "TEXT", allowableValues = {"TEXT", "VIDEO", "IMAGE", "DOCUMENT"}, required = true)
        private ContentType contentType;
        
        @NotEmpty(message = "Content cannot be empty")
        @Schema(description = "Content data - for TEXT type this is the text content, for others it should be a URL", 
                example = "Listen to the following audio and answer the questions below.", required = true)
        private String content;
    }
}