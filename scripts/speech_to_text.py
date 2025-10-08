#!/usr/bin/env python3
"""
Speech-to-Text conversion script for LMS
Converts MP3 audio files to text using speech recognition
"""

import sys
import os
import json
import tempfile
import argparse
from pathlib import Path

try:
    import speech_recognition as sr
    from pydub import AudioSegment
    import nltk
    from sentence_transformers import SentenceTransformer
    import numpy as np
    import openai
except ImportError as e:
    print(json.dumps({
        "success": False,
        "error": f"Missing required dependencies: {str(e)}",
        "message": "Please install required packages: pip install speechrecognition pydub nltk sentence-transformers openai"
    }))
    sys.exit(1)

class SpeechToTextProcessor:
    def __init__(self):
        self.recognizer = sr.Recognizer()
        # Initialize embedding model
        try:
            self.embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
        except Exception as e:
            print(json.dumps({
                "success": False,
                "error": f"Failed to load embedding model: {str(e)}"
            }))
            sys.exit(1)
    
    def convert_to_wav(self, input_path):
        """Convert MP3, MP4, or other audio/video formats to WAV format for speech recognition"""
        try:
            file_extension = Path(input_path).suffix.lower()
            
            # Load audio/video file based on format
            if file_extension == '.mp3':
                audio = AudioSegment.from_mp3(input_path)
            elif file_extension == '.mp4':
                # Extract audio from MP4 video
                audio = AudioSegment.from_file(input_path, format="mp4")
            elif file_extension == '.avi':
                audio = AudioSegment.from_file(input_path, format="avi")
            elif file_extension == '.mov':
                audio = AudioSegment.from_file(input_path, format="mov")
            elif file_extension == '.mkv':
                audio = AudioSegment.from_file(input_path, format="mkv")
            elif file_extension == '.flv':
                audio = AudioSegment.from_file(input_path, format="flv")
            elif file_extension == '.webm':
                audio = AudioSegment.from_file(input_path, format="webm")
            elif file_extension == '.wav':
                audio = AudioSegment.from_wav(input_path)
            elif file_extension == '.m4a':
                audio = AudioSegment.from_file(input_path, format="m4a")
            elif file_extension == '.aac':
                audio = AudioSegment.from_file(input_path, format="aac")
            else:
                # Try to load as generic audio/video file
                audio = AudioSegment.from_file(input_path)
            
            # Optimize audio for speech recognition
            # Convert to mono if stereo (reduces processing time)
            if audio.channels > 1:
                audio = audio.set_channels(1)
            
            # Normalize audio levels
            audio = audio.normalize()
            
            # Set sample rate to 16kHz for better speech recognition
            audio = audio.set_frame_rate(16000)
            
            # Convert to WAV and save to temporary file
            with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as temp_wav:
                audio.export(temp_wav.name, format="wav")
                return temp_wav.name, len(audio) / 1000.0  # Return duration in seconds
                
        except Exception as e:
            raise Exception(f"Error converting {file_extension} to WAV: {str(e)}")
    
    def transcribe_audio(self, input_path):
        """Transcribe audio/video file to text"""
        try:
            file_extension = Path(input_path).suffix.lower()
            duration_seconds = 0
            
            # Convert to WAV if not already in WAV format
            if file_extension != '.wav':
                wav_path, duration_seconds = self.convert_to_wav(input_path)
                cleanup_wav = True
            else:
                wav_path = input_path
                cleanup_wav = False
                # Get duration for WAV files
                try:
                    audio = AudioSegment.from_wav(wav_path)
                    duration_seconds = len(audio) / 1000.0
                except:
                    duration_seconds = 0
            
            # Check if audio is too long (limit to 10 minutes for free tier)
            if duration_seconds > 600:  # 10 minutes
                print(json.dumps({
                    "success": False,
                    "error": f"Audio duration ({duration_seconds:.1f}s) exceeds maximum limit of 10 minutes",
                    "message": "Please upload shorter audio/video files for processing"
                }))
                if cleanup_wav and os.path.exists(wav_path):
                    os.unlink(wav_path)
                return None
            
            # Transcribe audio in chunks if it's long (>1 minute)
            if duration_seconds > 60:
                text = self._transcribe_long_audio(wav_path, duration_seconds)
            else:
                text = self._transcribe_short_audio(wav_path)
            
            # Cleanup temporary WAV file if created
            if cleanup_wav and os.path.exists(wav_path):
                os.unlink(wav_path)
            
            return text, duration_seconds
            
        except Exception as e:
            raise Exception(f"Error transcribing audio: {str(e)}")
    
    def _transcribe_short_audio(self, wav_path):
        """Transcribe short audio files (<=1 minute)"""
        with sr.AudioFile(wav_path) as source:
            # Adjust for ambient noise
            self.recognizer.adjust_for_ambient_noise(source, duration=0.5)
            audio_data = self.recognizer.record(source)
        
        # Use Google Speech Recognition (free tier)
        try:
            return self.recognizer.recognize_google(audio_data)
        except sr.UnknownValueError:
            return "Could not understand audio"
        except sr.RequestError as e:
            # Fallback to offline recognition if available
            try:
                return self.recognizer.recognize_sphinx(audio_data)
            except:
                raise Exception(f"Speech recognition service error: {str(e)}")
    
    def _transcribe_long_audio(self, wav_path, duration_seconds):
        """Transcribe long audio files by breaking them into chunks"""
        try:
            # Load the audio file
            audio = AudioSegment.from_wav(wav_path)
            
            # Split audio into 30-second chunks
            chunk_length_ms = 30 * 1000  # 30 seconds
            chunks = []
            transcripts = []
            
            for i in range(0, len(audio), chunk_length_ms):
                chunk = audio[i:i + chunk_length_ms]
                
                # Save chunk to temporary file
                with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as temp_chunk:
                    chunk.export(temp_chunk.name, format="wav")
                    
                    # Transcribe the chunk
                    try:
                        chunk_text = self._transcribe_short_audio(temp_chunk.name)
                        if chunk_text and chunk_text != "Could not understand audio":
                            transcripts.append(chunk_text)
                    except Exception as e:
                        print(f"Warning: Failed to transcribe chunk {i//chunk_length_ms + 1}: {str(e)}")
                    finally:
                        # Cleanup chunk file
                        os.unlink(temp_chunk.name)
            
            if not transcripts:
                return "Could not understand audio"
            
            # Combine all transcripts
            full_transcript = " ".join(transcripts)
            
            # Clean up the transcript (remove duplicate phrases that might occur at chunk boundaries)
            words = full_transcript.split()
            cleaned_words = []
            for i, word in enumerate(words):
                # Simple duplicate removal - skip if same word appears within 3 positions
                if i < 3 or word.lower() not in [w.lower() for w in words[i-3:i]]:
                    cleaned_words.append(word)
            
            return " ".join(cleaned_words)
            
        except Exception as e:
            raise Exception(f"Error transcribing long audio: {str(e)}")
    
    def create_embeddings(self, text):
        """Create embeddings from text"""
        try:
            # Split text into sentences for better embeddings
            sentences = nltk.sent_tokenize(text)
            
            # Generate embeddings
            embeddings = self.embedding_model.encode(sentences)
            
            # Convert to list for JSON serialization
            embeddings_list = [embedding.tolist() for embedding in embeddings]
            
            return {
                "sentences": sentences,
                "embeddings": embeddings_list,
                "embedding_dimension": len(embeddings_list[0]) if embeddings_list else 0
            }
        except Exception as e:
            raise Exception(f"Error creating embeddings: {str(e)}")
    
    def generate_ai_summary(self, text):
        """Generate AI-powered summary of the text"""
        try:
            # Check if OpenAI API key is available
            api_key = os.getenv('OPENAI_API_KEY')
            if not api_key:
                # Fallback to basic summary
                sentences = nltk.sent_tokenize(text)
                if len(sentences) <= 3:
                    return text
                # Return first 3 sentences as basic summary
                return ' '.join(sentences[:3])
            
            # Use OpenAI for intelligent summarization
            client = openai.OpenAI(api_key=api_key)
            
            response = client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[
                    {
                        "role": "system",
                        "content": "You are an expert at summarizing audio transcripts for educational purposes. Create a concise, clear summary that captures the main points and key information."
                    },
                    {
                        "role": "user",
                        "content": f"Please summarize this audio transcript in 2-3 sentences, focusing on the main points:\n\n{text}"
                    }
                ],
                max_tokens=200,
                temperature=0.3
            )
            
            return response.choices[0].message.content.strip()
            
        except Exception as e:
            # Fallback to basic summary if AI fails
            sentences = nltk.sent_tokenize(text)
            if len(sentences) <= 3:
                return text
            return ' '.join(sentences[:3])

    def generate_questions(self, text, num_questions=5):
        """Generate IELTS-style listening comprehension questions based on the transcribed text"""
        try:
            # First, generate a summary
            summary = self.generate_ai_summary(text)
            
            # Check if OpenAI API key is available for advanced question generation
            api_key = os.getenv('OPENAI_API_KEY')
            if not api_key:
                return self._generate_basic_questions(text, summary, num_questions)
            
            # Use OpenAI for intelligent question generation
            client = openai.OpenAI(api_key=api_key)
            
            prompt = f"""
Based on this audio transcript and its summary, create {num_questions} IELTS-style listening comprehension questions.

AUDIO TRANSCRIPT:
{text}

SUMMARY:
{summary}

Create questions in these formats:
1. Multiple choice questions (4 options: A, B, C, D)
2. Fill-in-the-blank questions
3. True/False/Not Given questions
4. Short answer questions

For each question, provide:
- The question text
- Question type
- Correct answer
- Multiple choice options (if applicable)
- Difficulty level (Basic/Intermediate/Advanced)
- The specific part of the text that supports the answer

Format your response as JSON with this structure:
{{
    "summary": "summary text here",
    "questions": [
        {{
            "question": "question text",
            "type": "multiple_choice|fill_blank|true_false|short_answer",
            "difficulty": "Basic|Intermediate|Advanced",
            "correct_answer": "correct answer",
            "options": ["A) option1", "B) option2", "C) option3", "D) option4"],
            "context": "relevant text from transcript",
            "explanation": "why this is the correct answer"
        }}
    ]
}}
"""

            response = client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[
                    {
                        "role": "system",
                        "content": "You are an expert IELTS exam question writer. Create high-quality listening comprehension questions that test understanding, inference, and detail recognition. Always respond with valid JSON."
                    },
                    {
                        "role": "user",
                        "content": prompt
                    }
                ],
                max_tokens=1000,
                temperature=0.4
            )
            
            # Parse the AI response
            import json
            ai_response = json.loads(response.choices[0].message.content)
            
            return {
                "summary": ai_response.get("summary", summary),
                "questions": ai_response.get("questions", [])
            }
            
        except Exception as e:
            # Fallback to basic question generation
            return self._generate_basic_questions(text, summary, num_questions)

    def _generate_basic_questions(self, text, summary, num_questions=5):
        """Fallback method for basic question generation when AI is not available"""
        try:
            questions = []
            sentences = nltk.sent_tokenize(text)
            
            # Question templates for different types
            question_templates = [
                {
                    "type": "multiple_choice",
                    "template": "According to the audio, what is mentioned about {}?",
                    "difficulty": "Basic"
                },
                {
                    "type": "true_false", 
                    "template": "The audio states that {}",
                    "difficulty": "Basic"
                },
                {
                    "type": "fill_blank",
                    "template": "The speaker mentions that _______ {}",
                    "difficulty": "Intermediate"
                },
                {
                    "type": "short_answer",
                    "template": "What does the speaker say about {}?",
                    "difficulty": "Intermediate"
                }
            ]
            
            # Extract key phrases for question generation
            words = text.split()
            key_phrases = []
            
            # Find important nouns and phrases (basic NLP)
            for i, word in enumerate(words):
                if len(word) > 4 and word.lower() not in ['this', 'that', 'which', 'where', 'when', 'what']:
                    context = ' '.join(words[max(0, i-2):i+3])
                    key_phrases.append((word, context))
            
            # Generate questions using templates
            for i, (phrase, context) in enumerate(key_phrases[:num_questions]):
                template = question_templates[i % len(question_templates)]
                
                question_data = {
                    "question": template["template"].format(phrase.lower()),
                    "type": template["type"],
                    "difficulty": template["difficulty"],
                    "context": context,
                    "correct_answer": "Based on the audio content",
                    "explanation": f"This information can be found in the audio transcript."
                }
                
                # Add options for multiple choice
                if template["type"] == "multiple_choice":
                    question_data["options"] = [
                        f"A) {phrase} is mentioned positively",
                        f"B) {phrase} is not discussed",
                        f"C) {phrase} is mentioned with concern", 
                        f"D) {phrase} is mentioned briefly"
                    ]
                    question_data["correct_answer"] = "A"
                
                questions.append(question_data)
            
            # Add a summary question
            if len(text) > 100 and len(questions) < num_questions:
                questions.append({
                    "question": "What is the main topic discussed in this audio?",
                    "type": "short_answer",
                    "difficulty": "Basic",
                    "context": summary,
                    "correct_answer": "Based on the summary",
                    "explanation": "The main topic can be inferred from the overall content."
                })
            
            return {
                "summary": summary,
                "questions": questions[:num_questions]
            }
            
        except Exception as e:
            raise Exception(f"Error generating basic questions: {str(e)}")
    
    def process_audio_file(self, audio_path, generate_questions_flag=True, num_questions=5):
        """Main processing function"""
        try:
            # Verify file exists
            if not os.path.exists(audio_path):
                raise FileNotFoundError(f"Audio file not found: {audio_path}")
            
            # Transcribe audio/video to text
            transcription_result = self.transcribe_audio(audio_path)
            
            if transcription_result is None:
                return {
                    "success": False,
                    "error": "Audio processing failed - file may be too long or invalid"
                }
            
            if isinstance(transcription_result, tuple):
                transcribed_text, duration_seconds = transcription_result
            else:
                transcribed_text = transcription_result
                duration_seconds = 0
            
            if not transcribed_text or transcribed_text == "Could not understand audio":
                return {
                    "success": False,
                    "error": "Could not transcribe audio - audio may be unclear or empty"
                }
            
            # Create embeddings
            embedding_data = self.create_embeddings(transcribed_text)
            
            # Generate questions and summary if requested
            questions = []
            summary = ""
            if generate_questions_flag:
                question_data = self.generate_questions(transcribed_text, num_questions)
                if isinstance(question_data, dict):
                    questions = question_data.get("questions", [])
                    summary = question_data.get("summary", "")
                else:
                    questions = question_data
                    summary = self.generate_ai_summary(transcribed_text)
            
            return {
                "success": True,
                "transcribed_text": transcribed_text,
                "summary": summary,
                "word_count": len(transcribed_text.split()),
                "embeddings": embedding_data,
                "questions": questions,
                "metadata": {
                    "file_path": audio_path,
                    "file_size": os.path.getsize(audio_path),
                    "file_type": Path(audio_path).suffix.lower(),
                    "duration_seconds": duration_seconds,
                    "processed_at": str(Path().absolute())
                }
            }
            
        except Exception as e:
            return {
                "success": False,
                "error": str(e)
            }

def main():
    parser = argparse.ArgumentParser(description='Convert speech to text and generate questions from audio/video files')
    parser.add_argument('input_file', help='Path to the audio/video file (MP3, MP4, WAV, AVI, MOV, etc.)')
    parser.add_argument('--no-questions', action='store_true', help='Skip question generation')
    parser.add_argument('--num-questions', type=int, default=5, help='Number of questions to generate')
    parser.add_argument('--output', help='Output file path (default: stdout)')
    
    args = parser.parse_args()
    
    # Download required NLTK data
    try:
        nltk.download('punkt', quiet=True)
    except:
        pass  # Continue if download fails
    
    # Initialize processor
    processor = SpeechToTextProcessor()
    
    # Process the audio/video file
    result = processor.process_audio_file(
        args.input_file, 
        generate_questions_flag=not args.no_questions,
        num_questions=args.num_questions
    )
    
    # Output result
    json_output = json.dumps(result, indent=2)
    
    if args.output:
        with open(args.output, 'w') as f:
            f.write(json_output)
    else:
        print(json_output)

if __name__ == "__main__":
    main()