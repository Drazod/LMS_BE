# Speech-to-Text Feature Documentation

This feature allows users to upload audio files (MP3, WAV, M4A) or video files (MP4, AVI, MOV, MKV), extract the audio content, convert it to text using Python speech recognition, cr### Performance Considerations4. **"5. **"Speech recognition failed"**
   - Check audio quality (video files usually have good audio)
   - Ensure internet connection (for Google API)
   - Try with different audio/video file
   - Check if video has clear speech (not just music/background noise)o/Video conversion failed"**
   - Install ffmpeg (required for video processing)
   - Verify file format is supported
   - Check if video file has audio track
1. **File Size**: Video files are larger and take longer to process than audio files
2. **Video-to-Audio Conversion**: MP4 conversion adds processing time but is optimized
3. **Audio Duration**: Files longer than 1 minute are processed in 30-second chunks
4. **Audio Quality**: Clear audio provides better transcription (video files often have good audio)
5. **Timeout**: Default timeout is 5 minutes for processing
6. **Memory**: Video processing and embedding generation require additional memory
7. **Concurrent Processing**: Consider limiting concurrent uploads, especially for video files
8. **AI Features**: OpenAI-powered features add 2-3 seconds but provide much better questionsxt embeddings, and generate IELTS-style listening comprehension questions.

## Architecture Overview

```
Frontend (Audio/Video Upload) 
    ↓
Spring Boot Controller (/api/speech-to-text/upload)
    ↓
SpeechToTextService (Java)
    ↓
Python Script (speech_to_text.py)
    ↓
Video-to-Audio Conversion (if needed) → Speech Recognition → AI Summary → IELTS Questions
    ↓
JSON Response back to Frontend
```

## Features

1. **Media Processing**: Supports both audio (MP3, WAV, M4A, AAC) and video files (MP4, AVI, MOV, MKV, WebM)
2. **Audio Extraction**: Automatically extracts audio from video files and optimizes for speech recognition
3. **Speech Recognition**: Converts audio to text using Google Speech-to-Text API with chunking for long files
4. **AI Summarization**: Creates intelligent summaries using OpenAI GPT-3.5 (optional)
5. **IELTS-Style Questions**: Generates multiple choice, fill-blank, true/false, and short answer questions
6. **Text Embeddings**: Creates semantic embeddings using SentenceTransformers
7. **Smart Processing**: Handles long audio/video files by processing in chunks
8. **File Validation**: Validates file format, size, duration, and content type
9. **Error Handling**: Comprehensive error handling and logging

## Setup Instructions

### Prerequisites

1. **Java 17+** (for Spring Boot application)
2. **Python 3.7+** with pip
3. **ffmpeg** (for audio format conversion)

### Installation

1. **Run the setup script:**
   ```bash
   # For Linux/Mac
   chmod +x scripts/setup.sh
   ./scripts/setup.sh
   
   # For Windows
   scripts\setup.bat
   ```

2. **Manual setup (if script fails):**
   ```bash
   # Create virtual environment
   python -m venv venv
   
   # Activate virtual environment
   # Linux/Mac:
   source venv/bin/activate
   # Windows:
   venv\Scripts\activate
   
   # Install dependencies
   pip install -r scripts/requirements.txt
   
   # Download NLTK data
   python -c "import nltk; nltk.download('punkt')"
   ```

3. **Install system dependencies:**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install ffmpeg
   
   # macOS (with Homebrew)
   brew install ffmpeg
   
   # Windows (with Chocolatey)
   choco install ffmpeg
   ```

## API Endpoints

### 1. Upload Audio File

**POST** `/api/speech-to-text/upload`

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: multipart/form-data`

**Parameters:**
- `file` (required): Audio file (MP3, WAV, M4A, AAC) or Video file (MP4, AVI, MOV, MKV, WebM) - max 100MB
- `generateQuestions` (optional, default: true): Whether to generate IELTS-style questions
- `numQuestions` (optional, default: 5): Number of questions to generate (1-10)

**Response:**
```json
{
  "success": true,
  "message": "Audio processed successfully",
  "data": {
    "success": true,
    "transcribedText": "This is the transcribed text from the audio...",
    "summary": "AI-generated summary of the main points discussed",
    "wordCount": 42,
    "embeddings": {
      "sentences": ["Sentence 1.", "Sentence 2."],
      "embeddings": [[0.1, 0.2, ...], [0.3, 0.4, ...]],
      "embeddingDimension": 384
    },
    "questions": [
      {
        "question": "According to the audio, what are the main topics discussed?",
        "type": "multiple_choice",
        "difficulty": "Basic",
        "options": ["A) Topic 1", "B) Topic 2", "C) Topic 3", "D) All above"],
        "correctAnswer": "D",
        "context": "relevant transcript section",
        "explanation": "The audio covers all mentioned topics"
      }
    ],
    "metadata": {
      "originalFileName": "lecture_video.mp4",
      "fileSizeBytes": 1024000,
      "audioFormat": ".mp4",
      "audioDurationSeconds": 120.5,
      "processedAt": "2025-10-08T10:00:00"
    }
  }
}
```

### 2. Get Supported Formats

**GET** `/api/speech-to-text/supported-formats`

**Response:**
```json
{
  "success": true,
  "message": "Supported audio and video formats",
  "data": {
    "audioFormats": ["audio/mpeg", "audio/mp3", "audio/wav", "audio/m4a", "audio/aac"],
    "videoFormats": ["video/mp4", "video/avi", "video/quicktime", "video/webm"],
    "fileExtensions": [".mp3", ".wav", ".mp4", ".avi", ".mov", ".mkv", ".webm", ".m4a", ".aac"]
  }
}
```

### 3. Get Upload Limits

**GET** `/api/speech-to-text/limits`

**Response:**
```json
{
  "success": true,
  "message": "Upload limits and constraints",
  "data": {
    "maxFileSizeBytes": 104857600,
    "maxFileSizeMB": 100,
    "maxAudioDurationMinutes": 10,
    "supportedFormats": ["audio/mpeg", "video/mp4", "audio/wav", "video/avi"],
    "supportsVideoFiles": true,
    "maxQuestions": 10,
    "minQuestions": 1
  }
}
```

## Configuration

Add these properties to your `application.properties`:

```properties
# Speech-to-Text Configuration
app.python.executable=python
app.python.script.path=scripts/speech_to_text.py
app.temp.dir=${java.io.tmpdir}
app.python.timeout.seconds=300

# File upload limits
spring.servlet.multipart.max-file-size=200MB
spring.servlet.multipart.max-request-size=200MB
```

## Environment Variables

- `PYTHON_EXECUTABLE`: Path to Python executable (default: "python")
- `TEMP_DIR`: Temporary directory for file processing (default: system temp)

## Security

The endpoints are protected with JWT authentication and require one of these roles:
- `STUDENT`
- `INSTRUCTOR` 
- `ADMIN`

## Error Handling

Common error scenarios:

1. **File too large**: Returns 400 with message about 100MB size limit
2. **Invalid format**: Returns 400 with supported audio/video formats
3. **Audio too long**: Returns 400 if audio duration exceeds 10 minutes
4. **Video processing failure**: Returns 500 with video-to-audio conversion error
5. **Python script failure**: Returns 500 with error details
6. **Audio processing failure**: Returns 500 with transcription error
7. **Missing dependencies**: Returns 500 with installation instructions

## Dependencies

### Python Packages
- `speechrecognition`: For audio transcription
- `pydub`: For audio format conversion
- `nltk`: For natural language processing
- `sentence-transformers`: For text embeddings
- `numpy`: For numerical operations

### System Dependencies
- `ffmpeg`: For audio format conversion

## Performance Considerations

1. **File Size**: Larger files take longer to process
2. **Audio Quality**: Clear audio provides better transcription
3. **Timeout**: Default timeout is 5 minutes for processing
4. **Memory**: Embedding generation requires additional memory
5. **Concurrent Processing**: Consider limiting concurrent uploads

## Troubleshooting

### Common Issues

1. **"Python not found"**
   - Install Python 3.7+
   - Set `PYTHON_EXECUTABLE` environment variable

2. **"Module not found"**
   - Run the setup script
   - Activate virtual environment
   - Install requirements manually

3. **"Audio conversion failed"**
   - Install ffmpeg
   - Verify audio file format

4. **"Speech recognition failed"**
   - Check audio quality
   - Ensure internet connection (for Google API)
   - Try with different audio file

### Logs

Check application logs for detailed error information:
```bash
tail -f logs/application.log | grep SpeechToText
```

## Future Enhancements

1. **Multiple Language Support**: Add support for different languages
2. **Custom Models**: Use custom speech recognition models
3. **Advanced Question Types**: Generate different types of questions
4. **Audio Preprocessing**: Add noise reduction and enhancement
5. **Batch Processing**: Support multiple file uploads
6. **Real-time Processing**: WebSocket-based real-time transcription