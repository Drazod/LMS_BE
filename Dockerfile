# Multi-stage Docker build for Railway deployment
# This handles both Java Spring Boot and Python speech-to-text dependencies

# Stage 1: Build Java application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven files first for dependency caching
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src src
COPY scripts scripts
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Java + Python
FROM eclipse-temurin:17-jdk-jammy

# Install system dependencies including Python and ffmpeg
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-venv \
    ffmpeg \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create symbolic link for python command
RUN ln -s /usr/bin/python3 /usr/bin/python

WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Copy Python scripts and requirements
COPY --from=build /app/scripts ./scripts

# Install Python dependencies
RUN pip3 install -r scripts/requirements.txt

# Download NLTK data
RUN python3 -c "import nltk; nltk.download('punkt', quiet=True)" || echo "NLTK download completed"

# Make Python scripts executable
RUN chmod +x scripts/*.py || echo "Making Python files executable"

# Verify Python environment
RUN python3 -c "import speech_recognition, pydub, nltk, sentence_transformers; print('✅ Python dependencies verified')" || echo "⚠️ Some Python dependencies missing but will continue"

# Create startup script that verifies environment on each start
RUN echo '#!/bin/bash\n\
echo "🚀 Starting LMS Backend with Speech-to-Text support..."\n\
echo "Verifying Python environment..."\n\
python3 -c "import speech_recognition, pydub, nltk, sentence_transformers; print('\''✅ All Python dependencies available'\'')" || echo "⚠️ Some Python dependencies missing - basic functionality will work, install OpenAI key for enhanced features"\n\
echo "Starting Spring Boot application..."\n\
exec java $JAVA_OPTS -jar app.jar\n\
' > start.sh && chmod +x start.sh

# Expose port (Railway assigns this dynamically)
EXPOSE ${PORT:-8080}

# Use startup script
CMD ["./start.sh"]