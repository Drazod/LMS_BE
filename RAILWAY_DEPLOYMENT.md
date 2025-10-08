# Railway Deployment Guide for LMS with Speech-to-Text

This guide walks you through deploying your Spring Boot LMS application with speech-to-text functionality to Railway.

## 🚂 Railway Deployment Steps

### Prerequisites
- Railway account (https://railway.app)
- GitHub repository with your code
- Database setup (PostgreSQL recommended)

---

## Step 1: Prepare Your Repository

**✅ Your repository is already configured with:**
- `Dockerfile` - Handles both Java and Python dependencies
- `railway.toml` - Railway configuration
- Python scripts in `scripts/` directory
- Updated `application.properties` for Railway

**No additional setup needed!**

---

## Step 2: Deploy to Railway

### Option A: Deploy from GitHub (Recommended)

1. **Connect GitHub Repository:**
   ```
   - Go to https://railway.app
   - Click "Start a New Project"
   - Select "Deploy from GitHub repo"
   - Connect your GitHub account
   - Select your LMS repository
   ```

2. **Railway will automatically:**
   - Detect the Dockerfile
   - Build the multi-stage container (Java + Python)
   - Install all dependencies (no manual pip install needed!)
   - Deploy your application

### Option B: Deploy with Railway CLI

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Initialize project in your repo directory
cd your-lms-repo
railway init

# Deploy
railway up
```

---

## Step 3: Configure Environment Variables

In Railway Dashboard → Your Project → Variables, add:

### Required Variables:
```bash
# Database (Railway can provide PostgreSQL)
SPRING_DATASOURCE_URL=jdbc:postgresql://[host]:[port]/[database]
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password

# Application
PORT=8080  # Railway sets this automatically
BACKEND_HOST=https://your-app-name.up.railway.app/
```

### Optional Variables:
```bash
# Email (if using email features)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Enhanced AI Features (for better questions)
OPENAI_API_KEY=sk-your-openai-api-key

# Speech-to-Text Configuration (defaults are fine)
PYTHON_EXECUTABLE=python3
TEMP_DIR=/tmp
PYTHON_TIMEOUT=300
```

---

## Step 4: Database Setup

### Option A: Use Railway PostgreSQL (Recommended)
```
1. In Railway Dashboard → Add Service → PostgreSQL
2. Railway will provide connection variables automatically
3. Use the provided DATABASE_URL or individual connection variables
```

### Option B: External Database
```
Set the SPRING_DATASOURCE_* variables manually
```

---

## Step 5: Verify Deployment

### Check Application Health:
```bash
curl https://your-app-name.up.railway.app/api/health
```

### Check Speech-to-Text Endpoint:
```bash
curl https://your-app-name.up.railway.app/api/speech-to-text/supported-formats
```

### Test File Upload:
```bash
# Use Postman or frontend to test
POST https://your-app-name.up.railway.app/api/speech-to-text/upload
# Upload an MP3 or MP4 file
```

---

## 🐳 What the Dockerfile Does (Automatic)

**You don't need to manually install anything!** The Docker build:

1. **Installs System Dependencies:**
   - Java 17 JDK
   - Python 3 + pip
   - ffmpeg (for video processing)

2. **Installs Python Dependencies:**
   - speechrecognition
   - pydub
   - nltk
   - sentence-transformers
   - openai
   - numpy

3. **Downloads ML Models:**
   - NLTK punkt tokenizer
   - SentenceTransformers model

4. **Builds Java Application:**
   - Compiles Spring Boot application
   - Creates executable JAR

5. **Verifies Installation:**
   - Tests Python dependencies
   - Ensures speech-to-text works

---

## 🔍 Troubleshooting

### Build Issues:
```bash
# Check Railway build logs in dashboard
# Common issues:
1. Missing environment variables → Set in Railway dashboard
2. Database connection → Verify DATABASE_URL
3. Memory limits → Railway provides sufficient resources
```

### Runtime Issues:
```bash
# Check Railway deployment logs
# Test endpoints:
GET /api/health              # Application health
GET /api/speech-to-text/limits    # File upload limits
GET /api/speech-to-text/supported-formats  # Supported formats
```

### Python Dependencies:
```bash
# The Dockerfile handles this automatically
# If issues occur, check Railway logs for Python installation errors
# All dependencies are pre-installed during build
```

---

## 🚀 Expected Build Time

- **Initial Build:** 5-8 minutes (downloads and installs everything)
- **Subsequent Builds:** 2-3 minutes (cached layers)
- **Python Dependencies:** Installed automatically during build
- **ML Models:** Downloaded during build process

---

## 📊 Resource Usage

**Railway provides sufficient resources for:**
- Java Spring Boot application
- Python speech recognition
- ffmpeg video processing
- Machine learning models
- File uploads up to 100MB

---

## 🔒 Security Notes

1. **Environment Variables:** Set sensitive data in Railway dashboard (not in code)
2. **API Keys:** OpenAI key is optional but recommended for better questions
3. **File Uploads:** Limited to 100MB and 10 minutes duration
4. **Authentication:** JWT tokens required for endpoints

---

## ✅ Final Checklist

- [ ] Repository pushed to GitHub
- [ ] Railway project created and connected
- [ ] Environment variables configured
- [ ] Database connected
- [ ] Application deployed successfully
- [ ] Health endpoint responding
- [ ] Speech-to-text endpoints working
- [ ] File upload tested with MP3/MP4

**🎉 Your LMS with Speech-to-Text is now live on Railway!**

### Test URLs:
- Application: `https://your-app-name.up.railway.app`
- Health Check: `https://your-app-name.up.railway.app/api/health`
- API Docs: `https://your-app-name.up.railway.app/swagger-ui.html`
- Speech-to-Text: `https://your-app-name.up.railway.app/api/speech-to-text/upload`

---

## 🆘 Need Help?

1. **Railway Logs:** Check build and deployment logs in Railway dashboard
2. **Application Logs:** Monitor runtime logs for errors
3. **Test Endpoints:** Use Postman to test API endpoints
4. **GitHub Issues:** Check for any code-related issues

**The deployment is fully automated - Railway handles all Python dependencies, system packages, and configuration automatically!**