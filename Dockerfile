# Multi-stage Docker build for Railway deployment
# Optimized for faster builds with better caching

# Stage 1: Build Java application
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
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

# Stage 2: Python dependencies pre-build (for caching)
FROM python:3.10-slim AS python-deps
WORKDIR /app

# Copy requirements first for better caching
COPY scripts/requirements.txt ./requirements.txt

# Install system dependencies for Python packages (minimal)
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    && rm -rf /var/lib/apt/lists/*

# Install Python dependencies with caching
RUN pip3 install --no-cache-dir --user -r requirements.txt

# Download NLTK data
RUN python3 -c "import nltk; nltk.download('punkt', quiet=True)" || echo "NLTK download completed"

# Stage 3: Runtime (lightweight)
FROM eclipse-temurin:17-jre-jammy

# Install only essential runtime dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    python3-distutils \
    ffmpeg \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && ln -s /usr/bin/python3 /usr/bin/python

WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Copy Python scripts
COPY --from=build /app/scripts ./scripts

# Copy pre-installed Python dependencies
COPY --from=python-deps /root/.local /root/.local

# Update PATH to include local Python packages
ENV PATH=/root/.local/bin:$PATH
ENV PYTHONPATH=/root/.local/lib/python3.10/site-packages:$PYTHONPATH

# Make Python scripts executable
RUN chmod +x scripts/*.py || echo "Making Python files executable"

# Verify Python environment quickly
RUN python3 -c "import speech_recognition, pydub; print('✅ Core dependencies verified')"

# Create optimized startup script
RUN echo '#!/bin/bash\n\
echo "🚀 Starting LMS Backend..."\n\
exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar\n\
' > start.sh && chmod +x start.sh

# Expose port
EXPOSE 8080

# Use startup script with optimized JVM flags
CMD ["./start.sh"]