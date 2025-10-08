package com.curcus.lms.service.impl;

import com.curcus.lms.model.response.SpeechToTextResponse;
import com.curcus.lms.service.SpeechToTextService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SpeechToTextServiceImpl implements SpeechToTextService {

    private static final Logger logger = LoggerFactory.getLogger(SpeechToTextServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.python.executable:python}")
    private String pythonExecutable;

    @Value("${app.python.script.path:scripts/speech_to_text.py}")
    private String pythonScriptPath;

    @Value("${app.temp.dir:${java.io.tmpdir}}")
    private String tempDir;

    @Value("${app.python.timeout.seconds:300}")
    private long pythonTimeoutSeconds;

    @Override
    public SpeechToTextResponse processAudioFile(MultipartFile audioFile, boolean generateQuestions, int numQuestions) throws Exception {
        logger.info("Processing audio file: {} (size: {} bytes)", audioFile.getOriginalFilename(), audioFile.getSize());

        // Create temporary file to store the uploaded audio
        String tempFileName = UUID.randomUUID().toString() + "_" + audioFile.getOriginalFilename();
        Path tempFilePath = Paths.get(tempDir, tempFileName);

        try {
            // Save uploaded file to temporary location
            Files.copy(audioFile.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("Saved temporary file: {}", tempFilePath);

            // Execute Python script
            String pythonOutput = executePythonScript(tempFilePath.toString(), generateQuestions, numQuestions);
            logger.debug("Python script output received");

            // Parse Python script output
            SpeechToTextResponse response = parsePythonOutput(pythonOutput);
            logger.info("Successfully processed audio file. Transcribed text length: {} characters", 
                       response.getTranscribedText() != null ? response.getTranscribedText().length() : 0);

            return response;

        } finally {
            // Cleanup temporary file
            try {
                Files.deleteIfExists(tempFilePath);
                logger.debug("Cleaned up temporary file: {}", tempFilePath);
            } catch (IOException e) {
                logger.warn("Failed to cleanup temporary file: {}", tempFilePath, e);
            }
        }
    }

    @Override
    public String executePythonScript(String filePath, boolean generateQuestions, int numQuestions) throws Exception {
        logger.debug("Executing Python script with file: {}", filePath);

        // Build command
        List<String> command = new ArrayList<>();
        command.add(pythonExecutable);
        command.add(resolvePythonScriptPath());
        command.add(filePath);
        
        if (!generateQuestions) {
            command.add("--no-questions");
        } else {
            command.add("--num-questions");
            command.add(String.valueOf(numQuestions));
        }

        logger.debug("Executing command: {}", String.join(" ", command));

        // Execute the command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        // Read output
        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
        }

        // Wait for process completion with timeout
        boolean finished = process.waitFor(pythonTimeoutSeconds, TimeUnit.SECONDS);
        
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Python script execution timed out after " + pythonTimeoutSeconds + " seconds");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            logger.error("Python script failed with exit code: {}. Error output: {}", exitCode, errorOutput.toString());
            throw new RuntimeException("Python script execution failed with exit code " + exitCode + ": " + errorOutput.toString());
        }

        String result = output.toString().trim();
        logger.debug("Python script execution completed successfully");
        return result;
    }

    private String resolvePythonScriptPath() {
        // Try to resolve the script path relative to the project root
        Path scriptPath = Paths.get(pythonScriptPath);
        
        if (Files.exists(scriptPath)) {
            return scriptPath.toAbsolutePath().toString();
        }
        
        // Try relative to current working directory
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path relativePath = currentDir.resolve(pythonScriptPath);
        
        if (Files.exists(relativePath)) {
            return relativePath.toAbsolutePath().toString();
        }
        
        // Return the original path and let the system handle it
        logger.warn("Python script not found at expected locations. Using: {}", pythonScriptPath);
        return pythonScriptPath;
    }

    private SpeechToTextResponse parsePythonOutput(String pythonOutput) throws Exception {
        try {
            JsonNode rootNode = objectMapper.readTree(pythonOutput);
            
            if (!rootNode.get("success").asBoolean()) {
                String error = rootNode.has("error") ? rootNode.get("error").asText() : "Unknown error";
                throw new RuntimeException("Python script failed: " + error);
            }

            SpeechToTextResponse response = new SpeechToTextResponse();
            response.setSuccess(true);
            response.setTranscribedText(rootNode.get("transcribed_text").asText());
            response.setSummary(rootNode.has("summary") ? rootNode.get("summary").asText() : null);
            response.setWordCount(rootNode.get("word_count").asInt());

            // Parse embeddings
            if (rootNode.has("embeddings")) {
                JsonNode embeddingsNode = rootNode.get("embeddings");
                SpeechToTextResponse.EmbeddingData embeddingData = new SpeechToTextResponse.EmbeddingData();
                
                // Parse sentences
                JsonNode sentencesNode = embeddingsNode.get("sentences");
                List<String> sentences = new ArrayList<>();
                if (sentencesNode.isArray()) {
                    for (JsonNode sentenceNode : sentencesNode) {
                        sentences.add(sentenceNode.asText());
                    }
                }
                embeddingData.setSentences(sentences);
                
                // Parse embeddings arrays
                JsonNode embeddingsArrayNode = embeddingsNode.get("embeddings");
                List<List<Double>> embeddings = new ArrayList<>();
                if (embeddingsArrayNode.isArray()) {
                    for (JsonNode embeddingNode : embeddingsArrayNode) {
                        List<Double> embedding = new ArrayList<>();
                        if (embeddingNode.isArray()) {
                            for (JsonNode valueNode : embeddingNode) {
                                embedding.add(valueNode.asDouble());
                            }
                        }
                        embeddings.add(embedding);
                    }
                }
                embeddingData.setEmbeddings(embeddings);
                embeddingData.setEmbeddingDimension(embeddingsNode.get("embedding_dimension").asInt());
                
                response.setEmbeddings(embeddingData);
            }

            // Parse questions
            if (rootNode.has("questions")) {
                JsonNode questionsNode = rootNode.get("questions");
                List<SpeechToTextResponse.GeneratedQuestion> questions = new ArrayList<>();
                
                if (questionsNode.isArray()) {
                    for (JsonNode questionNode : questionsNode) {
                        SpeechToTextResponse.GeneratedQuestion question = new SpeechToTextResponse.GeneratedQuestion();
                        question.setQuestion(questionNode.get("question").asText());
                        question.setType(questionNode.has("type") ? questionNode.get("type").asText() : "comprehension");
                        question.setDifficulty(questionNode.has("difficulty") ? questionNode.get("difficulty").asText() : "Basic");
                        question.setCorrectAnswer(questionNode.has("correct_answer") ? questionNode.get("correct_answer").asText() : null);
                        question.setContext(questionNode.has("context") ? questionNode.get("context").asText() : null);
                        question.setExplanation(questionNode.has("explanation") ? questionNode.get("explanation").asText() : null);
                        
                        // Parse options for multiple choice questions
                        if (questionNode.has("options")) {
                            JsonNode optionsNode = questionNode.get("options");
                            List<String> options = new ArrayList<>();
                            if (optionsNode.isArray()) {
                                for (JsonNode optionNode : optionsNode) {
                                    options.add(optionNode.asText());
                                }
                            }
                            question.setOptions(options);
                        }
                        
                        questions.add(question);
                    }
                }
                response.setQuestions(questions);
            }

            // Parse metadata
            if (rootNode.has("metadata")) {
                JsonNode metadataNode = rootNode.get("metadata");
                SpeechToTextResponse.ProcessingMetadata metadata = new SpeechToTextResponse.ProcessingMetadata();
                metadata.setOriginalFileName(metadataNode.has("file_path") ? 
                    Paths.get(metadataNode.get("file_path").asText()).getFileName().toString() : "unknown");
                metadata.setFileSizeBytes(metadataNode.has("file_size") ? metadataNode.get("file_size").asLong() : 0L);
                metadata.setAudioFormat(metadataNode.has("file_type") ? metadataNode.get("file_type").asText() : "unknown");
                metadata.setAudioDurationSeconds(metadataNode.has("duration_seconds") ? metadataNode.get("duration_seconds").asDouble() : null);
                metadata.setProcessedAt(java.time.LocalDateTime.now());
                response.setMetadata(metadata);
            }

            return response;

        } catch (Exception e) {
            logger.error("Failed to parse Python script output: {}", pythonOutput, e);
            throw new RuntimeException("Failed to parse Python script output: " + e.getMessage(), e);
        }
    }
}