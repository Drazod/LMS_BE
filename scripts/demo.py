#!/usr/bin/env python3
"""
Example usage of the enhanced speech-to-text script with AI-powered question generation
"""

import sys
import os
import json

# Add the scripts directory to the path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from speech_to_text import SpeechToTextProcessor

def main():
    print("🎯 Enhanced Speech-to-Text with AI Question Generation Demo")
    print("=" * 60)
    
    # Check if OpenAI API key is set
    api_key = os.getenv('OPENAI_API_KEY')
    if api_key:
        print("✅ OpenAI API key found - AI-powered features enabled")
    else:
        print("⚠️  OpenAI API key not found - using basic question generation")
        print("   Set OPENAI_API_KEY environment variable for enhanced features")
    
    print()
    
    # Example text for demonstration (simulating transcribed audio)
    example_text = """
    Welcome to today's lecture on renewable energy sources. In this session, we will discuss 
    the three main types of renewable energy: solar, wind, and hydroelectric power. 
    
    Solar energy is harnessed using photovoltaic panels that convert sunlight directly into 
    electricity. This technology has become increasingly efficient and cost-effective over 
    the past decade. Many countries are now investing heavily in solar farms to reduce their 
    carbon footprint.
    
    Wind energy, on the other hand, uses turbines to capture the kinetic energy of moving air. 
    Wind farms are typically located in areas with consistent wind patterns, such as coastal 
    regions or open plains. The technology has advanced significantly, with modern turbines 
    being much more efficient than their predecessors.
    
    Lastly, hydroelectric power generates electricity by harnessing the energy of flowing water. 
    This can be achieved through large dams or smaller run-of-river systems. Hydroelectric 
    power is one of the oldest forms of renewable energy and continues to be a major source 
    of clean electricity worldwide.
    
    Each of these renewable energy sources has its own advantages and challenges. Solar energy 
    is abundant but depends on weather conditions. Wind energy is clean but can be intermittent. 
    Hydroelectric power is reliable but may have environmental impacts on river ecosystems.
    """
    
    print("📝 Sample Audio Transcript:")
    print("-" * 40)
    print(example_text[:200] + "..." if len(example_text) > 200 else example_text)
    print()
    
    # Initialize processor
    processor = SpeechToTextProcessor()
    
    try:
        print("🔄 Processing text and generating questions...")
        
        # Generate embeddings
        embeddings = processor.create_embeddings(example_text)
        print(f"✅ Created embeddings: {len(embeddings['sentences'])} sentences, {embeddings['embedding_dimension']} dimensions")
        
        # Generate questions
        question_data = processor.generate_questions(example_text, num_questions=5)
        
        if isinstance(question_data, dict):
            summary = question_data.get("summary", "")
            questions = question_data.get("questions", [])
        else:
            questions = question_data
            summary = processor.generate_ai_summary(example_text)
        
        print()
        print("📊 AI-Generated Summary:")
        print("-" * 30)
        print(summary)
        print()
        
        print("❓ Generated IELTS-Style Questions:")
        print("-" * 40)
        
        for i, question in enumerate(questions, 1):
            print(f"\n{i}. {question.get('question', 'N/A')}")
            print(f"   Type: {question.get('type', 'N/A')}")
            print(f"   Difficulty: {question.get('difficulty', 'N/A')}")
            
            if question.get('options'):
                print("   Options:")
                for option in question['options']:
                    print(f"      {option}")
            
            if question.get('correct_answer'):
                print(f"   Correct Answer: {question['correct_answer']}")
            
            if question.get('explanation'):
                print(f"   Explanation: {question['explanation']}")
        
        print("\n" + "=" * 60)
        print("✅ Demo completed successfully!")
        
        if not api_key:
            print("\n💡 Pro Tip: Set your OpenAI API key to see enhanced AI-powered questions!")
            print("   export OPENAI_API_KEY='your-api-key-here'")
        
    except Exception as e:
        print(f"❌ Error during processing: {str(e)}")
        return 1
    
    return 0

if __name__ == "__main__":
    sys.exit(main())