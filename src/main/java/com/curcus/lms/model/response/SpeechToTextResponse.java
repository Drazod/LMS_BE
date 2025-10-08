package com.curcus.lms.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpeechToTextResponse {
    
    private boolean success;
    private String message;
    private String transcribedText;
    private String summary;
    private Integer wordCount;
    private EmbeddingData embeddings;
    private List<GeneratedQuestion> questions;
    private ProcessingMetadata metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EmbeddingData {
        private List<String> sentences;
        private List<List<Double>> embeddings;
        private Integer embeddingDimension;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GeneratedQuestion {
        private String question;
        private String type; // multiple_choice, fill_blank, true_false, short_answer
        private String difficulty; // Basic, Intermediate, Advanced
        private String correctAnswer;
        private List<String> options; // For multiple choice questions
        private String context; // Relevant part of the transcript
        private String explanation; // Why this is the correct answer
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProcessingMetadata {
        private String originalFileName;
        private Long fileSizeBytes;
        private String audioFormat;
        private Double audioDurationSeconds;
        private LocalDateTime processedAt;
        private Long processingTimeMs;
    }
}