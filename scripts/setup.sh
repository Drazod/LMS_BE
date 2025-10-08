#!/bin/bash

# Setup script for Speech-to-Text Python environment
# This script sets up the Python environment required for speech-to-text functionality

echo "Setting up Python environment for Speech-to-Text functionality..."

# Check if Python is installed
if ! command -v python3 &> /dev/null && ! command -v python &> /dev/null; then
    echo "Error: Python is not installed. Please install Python 3.7+ first."
    exit 1
fi

# Use python3 if available, otherwise python
PYTHON_CMD="python3"
if ! command -v python3 &> /dev/null; then
    PYTHON_CMD="python"
fi

echo "Using Python command: $PYTHON_CMD"

# Check Python version
$PYTHON_CMD --version

# Create virtual environment if it doesn't exist
if [ ! -d "venv" ]; then
    echo "Creating virtual environment..."
    $PYTHON_CMD -m venv venv
fi

# Activate virtual environment
echo "Activating virtual environment..."
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    # Windows
    source venv/Scripts/activate
else
    # Linux/Mac
    source venv/bin/activate
fi

# Upgrade pip
echo "Upgrading pip..."
pip install --upgrade pip

# Install required packages
echo "Installing required Python packages..."
pip install -r scripts/requirements.txt

# Download NLTK data
echo "Downloading NLTK data..."
$PYTHON_CMD -c "
import nltk
try:
    nltk.download('punkt', quiet=True)
    print('NLTK punkt data downloaded successfully')
except Exception as e:
    print(f'Warning: Could not download NLTK data: {e}')
"

# Test the speech-to-text script
echo "Testing speech-to-text script..."
if [ -f "scripts/speech_to_text.py" ]; then
    $PYTHON_CMD scripts/speech_to_text.py --help
    if [ $? -eq 0 ]; then
        echo "✅ Speech-to-text script is working correctly!"
    else
        echo "❌ There was an issue with the speech-to-text script."
        exit 1
    fi
else
    echo "❌ speech_to_text.py not found in scripts directory"
    exit 1
fi

echo ""
echo "Setup completed successfully! 🎉"
echo ""
echo "To use the speech-to-text functionality:"
echo "1. Make sure the virtual environment is activated: source venv/bin/activate (Linux/Mac) or venv\\Scripts\\activate (Windows)"
echo "2. Run your Spring Boot application"
echo "3. Use the /api/speech-to-text/upload endpoint to upload MP3 files"
echo ""
echo "Note: For production deployment, make sure to:"
echo "- Install ffmpeg system package for audio conversion"
echo "- Set the PYTHON_EXECUTABLE environment variable if needed"
echo "- Configure appropriate file upload limits"