package com.curcus.lms.controller;

import com.curcus.lms.model.response.ApiResponse;
import com.curcus.lms.model.response.SpeechToTextResponse;
import com.curcus.lms.service.SpeechToTextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/speech-to-text")
@Validated
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Speech to Text", description = "Speech to text conversion and question generation APIs")
public class SpeechToTextController {

    @Autowired
    private SpeechToTextService speechToTextService;

    private static final List<String> ALLOWED_MEDIA_TYPES = Arrays.asList(
            // Audio formats
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/wave", "audio/x-wav", 
            "audio/m4a", "audio/aac", "audio/ogg", "audio/flac", "audio/mp4",
            // Video formats - including common variations
            "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo", 
            "video/x-ms-wmv", "video/webm", "video/x-flv", "video/x-matroska",
            // Additional MP4 variations
            "video/mp4v-es", "application/mp4", "application/octet-stream"
    );

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB (increased for video files)

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload audio/video file and convert to text",
            description = "Upload an audio file (MP3, WAV, M4A) or video file (MP4, AVI, MOV), extract audio, convert to text, create embeddings, and optionally generate IELTS-style questions"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Audio processed successfully",
                    content = @Content(schema = @Schema(implementation = SpeechToTextResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid file format or size"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Processing error"
            )
    })
    // @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')") // Temporarily disabled for testing
    public ResponseEntity<ApiResponse<SpeechToTextResponse>> uploadAndProcessAudio(
            @Parameter(description = "Audio/Video file (MP3, WAV, MP4, AVI, MOV formats, max 100MB)")
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Generate questions from the transcribed text")
            @RequestParam(value = "generateQuestions", defaultValue = "true") boolean generateQuestions,
            
            @Parameter(description = "Number of questions to generate (1-10)")
            @RequestParam(value = "numQuestions", defaultValue = "5") 
            @Min(value = 1, message = "Number of questions must be at least 1")
            @Max(value = 10, message = "Number of questions cannot exceed 10") 
            int numQuestions) {

        try {
            // Validate file
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("errorCode", "400");
                error.put("errorMessage", "No file provided");
                ApiResponse<SpeechToTextResponse> response = new ApiResponse<>();
                response.error(error);
                return ResponseEntity.badRequest().body(response);
            }

            // Check file size
            if (file.getSize() > MAX_FILE_SIZE) {
                Map<String, String> error = new HashMap<>();
                error.put("errorCode", "400");
                error.put("errorMessage", "File size exceeds maximum allowed size of 100MB");
                ApiResponse<SpeechToTextResponse> response = new ApiResponse<>();
                response.error(error);
                return ResponseEntity.badRequest().body(response);
            }

            // Check file type
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            
            // Enhanced file type validation - prioritize file extension over MIME type
            boolean isValidFile = false;
            
            // First, check file extension (more reliable)
            if (fileName != null) {
                String fileExtension = fileName.toLowerCase();
                isValidFile = fileExtension.endsWith(".mp3") || fileExtension.endsWith(".wav") ||
                             fileExtension.endsWith(".mp4") || fileExtension.endsWith(".avi") ||
                             fileExtension.endsWith(".mov") || fileExtension.endsWith(".mkv") ||
                             fileExtension.endsWith(".webm") || fileExtension.endsWith(".flv") ||
                             fileExtension.endsWith(".m4a") || fileExtension.endsWith(".aac");
            }
            
            // If extension check fails, fallback to MIME type
            if (!isValidFile && contentType != null) {
                isValidFile = ALLOWED_MEDIA_TYPES.contains(contentType.toLowerCase());
            }
            
            if (!isValidFile) {
                // Log the actual MIME type for debugging
                System.err.println("Rejected file - MIME type: " + contentType + ", File name: " + fileName);
                
                Map<String, String> error = new HashMap<>();
                error.put("errorCode", "400");
                error.put("errorMessage", String.format(
                    "Invalid file format. Received MIME type: %s, File: %s. Supported formats: MP3, WAV, MP4, AVI, MOV, MKV, WebM, M4A, AAC", 
                    contentType, fileName
                ));
                ApiResponse<SpeechToTextResponse> response = new ApiResponse<>();
                response.error(error);
                return ResponseEntity.badRequest().body(response);
            }

            // Process the audio file
            SpeechToTextResponse result = speechToTextService.processAudioFile(
                    file, generateQuestions, numQuestions
            );

            String fileType = fileName != null && fileName.toLowerCase().matches(".*\\.(mp4|avi|mov|mkv|webm|flv)$") ? "video" : "audio";
            ApiResponse<SpeechToTextResponse> response = new ApiResponse<>();
            response.ok(result);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("errorCode", "500");
            error.put("errorMessage", "Error processing audio file: " + e.getMessage());
            ApiResponse<SpeechToTextResponse> response = new ApiResponse<>();
            response.error(error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/test-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Test file upload validation",
            description = "Test endpoint to validate file upload without processing - for debugging"
    )
    public ResponseEntity<ApiResponse<Object>> testFileUpload(
            @RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            long fileSize = file.getSize();
            
            Map<String, Object> info = new HashMap<>();
            info.put("fileName", fileName);
            info.put("contentType", contentType);
            info.put("fileSize", fileSize);
            info.put("fileSizeMB", fileSize / (1024.0 * 1024.0));
            
            // File extension check
            boolean hasValidExtension = false;
            if (fileName != null) {
                String fileExtension = fileName.toLowerCase();
                hasValidExtension = fileExtension.endsWith(".mp3") || fileExtension.endsWith(".wav") ||
                                   fileExtension.endsWith(".mp4") || fileExtension.endsWith(".avi") ||
                                   fileExtension.endsWith(".mov") || fileExtension.endsWith(".mkv") ||
                                   fileExtension.endsWith(".webm") || fileExtension.endsWith(".flv") ||
                                   fileExtension.endsWith(".m4a") || fileExtension.endsWith(".aac");
            }
            
            // MIME type check
            boolean hasValidMimeType = contentType != null && ALLOWED_MEDIA_TYPES.contains(contentType.toLowerCase());
            
            info.put("hasValidExtension", hasValidExtension);
            info.put("hasValidMimeType", hasValidMimeType);
            info.put("allowedMimeTypes", ALLOWED_MEDIA_TYPES);
            info.put("isValidFile", hasValidExtension || hasValidMimeType);
            info.put("isEmpty", file.isEmpty());
            info.put("exceedsMaxSize", fileSize > MAX_FILE_SIZE);
            
            ApiResponse<Object> response = new ApiResponse<>();
            response.ok(info);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("errorCode", "500");
            error.put("errorMessage", "Error testing file: " + e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>();
            response.error(error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/supported-formats")
    @Operation(
            summary = "Get supported audio and video formats",
            description = "Returns a list of supported audio and video file formats"
    )
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getSupportedFormats() {
        Object data = java.util.Map.of(
                "allFormats", ALLOWED_MEDIA_TYPES,
                "audioFormats", Arrays.asList("audio/mpeg", "audio/mp3", "audio/wav", "audio/m4a", "audio/aac"),
                "videoFormats", Arrays.asList("video/mp4", "video/avi", "video/quicktime", "video/webm", "video/x-matroska"),
                "fileExtensions", Arrays.asList(".mp3", ".wav", ".mp4", ".avi", ".mov", ".mkv", ".webm", ".m4a", ".aac")
        );
        ApiResponse<Object> response = new ApiResponse<>();
        response.ok(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/limits")
    @Operation(
            summary = "Get upload limits",
            description = "Returns information about file size limits and other constraints"
    )
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getUploadLimits() {
        Object data = java.util.Map.of(
                "maxFileSizeBytes", MAX_FILE_SIZE,
                "maxFileSizeMB", MAX_FILE_SIZE / (1024 * 1024),
                "supportedFormats", ALLOWED_MEDIA_TYPES,
                "maxAudioDurationMinutes", 10,
                "maxQuestions", 10,
                "minQuestions", 1,
                "supportsVideoFiles", true
        );
        ApiResponse<Object> response = new ApiResponse<>();
        response.ok(data);
        return ResponseEntity.ok(response);
    }
}