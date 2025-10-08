@echo off
REM Setup script for Speech-to-Text Python environment (Windows)
REM This script sets up the Python environment required for speech-to-text functionality

echo Setting up Python environment for Speech-to-Text functionality...

REM Check if Python is installed
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Python is not installed. Please install Python 3.7+ first.
    exit /b 1
)

echo Using Python:
python --version

REM Create virtual environment if it doesn't exist
if not exist "venv" (
    echo Creating virtual environment...
    python -m venv venv
)

REM Activate virtual environment
echo Activating virtual environment...
call venv\Scripts\activate.bat

REM Upgrade pip
echo Upgrading pip...
python -m pip install --upgrade pip

REM Install required packages
echo Installing required Python packages...
pip install -r scripts\requirements.txt

REM Download NLTK data
echo Downloading NLTK data...
python -c "import nltk; nltk.download('punkt', quiet=True); print('NLTK punkt data downloaded successfully')"

REM Test the speech-to-text script
echo Testing speech-to-text script...
if exist "scripts\speech_to_text.py" (
    python scripts\speech_to_text.py --help
    if %errorlevel% equ 0 (
        echo ✅ Speech-to-text script is working correctly!
    ) else (
        echo ❌ There was an issue with the speech-to-text script.
        exit /b 1
    )
) else (
    echo ❌ speech_to_text.py not found in scripts directory
    exit /b 1
)

echo.
echo Setup completed successfully! 🎉
echo.
echo To use the speech-to-text functionality:
echo 1. Make sure the virtual environment is activated: venv\Scripts\activate.bat
echo 2. Run your Spring Boot application
echo 3. Use the /api/speech-to-text/upload endpoint to upload MP3 files
echo.
echo Note: For production deployment, make sure to:
echo - Install ffmpeg system package for audio conversion
echo - Set the PYTHON_EXECUTABLE environment variable if needed
echo - Configure appropriate file upload limits

pause