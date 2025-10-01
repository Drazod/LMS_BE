package com.curcus.lms.model.dto;

import com.curcus.lms.constants.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionContentItem {
    private ContentType contentType;
    private String textContent;
    private MultipartFile file;
    private Long position;
    
    public boolean isFileContent() {
        return file != null && (contentType == ContentType.IMAGE || contentType == ContentType.VIDEO);
    }
    
    public boolean isTextContent() {
        return textContent != null && !textContent.trim().isEmpty();
    }
}