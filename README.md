# BFHL REST API

A production-grade REST API built with **Java 17** and **Spring Boot 3.5** for the Bajaj Finserv Health qualifier.

## Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/bfhl`  | POST   | Processes one of: `fibonacci`, `prime`, `lcm`, `hcf`, `AI` |
| `/health`| GET    | Health check |

## Tech Stack

- Java 17, Spring Boot 3.5.10
- Google Gemini 2.5 Flash (AI integration)
- Bucket4j (rate limiting)
- Docker-ready (multi-stage build)

## Run Locally

```bash
# Set your Gemini API key
export GEMINI_API_KEY=your_key_here

# Build & run
./mvnw clean package -DskipTests
java -jar target/bfhl-api-0.0.1-SNAPSHOT.jar
```

Server starts at `http://localhost:8080`.

## API Examples

```bash
# Fibonacci
curl -X POST http://localhost:8080/bfhl -H 'Content-Type: application/json' \
  -d '{"fibonacci": 7}'

# Prime filter
curl -X POST http://localhost:8080/bfhl -H 'Content-Type: application/json' \
  -d '{"prime": [2,4,7,9,11]}'

# LCM
curl -X POST http://localhost:8080/bfhl -H 'Content-Type: application/json' \
  -d '{"lcm": [12,18,24]}'

# HCF
curl -X POST http://localhost:8080/bfhl -H 'Content-Type: application/json' \
  -d '{"hcf": [24,36,60]}'

# AI question
curl -X POST http://localhost:8080/bfhl -H 'Content-Type: application/json' \
  -d '{"AI": "What is the capital city of Maharashtra?"}'

# Health check
curl http://localhost:8080/health
```

## Deploy

### Railway
1. Push to GitHub
2. [railway.app](https://railway.app) → New Project → Deploy from GitHub
3. Add env var: `GEMINI_API_KEY`
4. Deploy

### Render
1. Push to GitHub
2. [render.com](https://render.com) → New Web Service → Select repo
3. Build: `./mvnw clean package -DskipTests`
4. Start: `java -jar target/bfhl-api-0.0.1-SNAPSHOT.jar`
5. Add env var: `GEMINI_API_KEY`
