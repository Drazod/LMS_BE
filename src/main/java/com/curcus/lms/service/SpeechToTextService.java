package com.curcus.lms.service;

import com.curcus.lms.model.response.SpeechToTextResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SpeechToTextService {
    
    /**
     * Process an audio file by converting it to text, creating embeddings, and optionally generating questions
     * 
     * @param audioFile The uploaded audio file
     * @param generateQuestions Whether to generate questions from the transcribed text
     * @param numQuestions Number of questions to generate
     * @return SpeechToTextResponse containing the processed results
     * @throws Exception if processing fails
     */
    SpeechToTextResponse processAudioFile(MultipartFile audioFile, boolean generateQuestions, int numQuestions) throws Exception;
    
    /**
     * Execute the Python speech-to-text script
     * 
     * @param filePath Path to the audio file
     * @param generateQuestions Whether to generate questions
     * @param numQuestions Number of questions to generate
     * @return Raw output from the Python script
     * @throws Exception if execution fails
     */
    String executePythonScript(String filePath, boolean generateQuestions, int numQuestions) throws Exception;
    
    /**
     * Test Python environment and script availability
     * 
     * @return Test result information
     * @throws Exception if test fails
     */
    String testPythonEnvironment() throws Exception;
}