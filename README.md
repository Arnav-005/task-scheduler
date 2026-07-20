# Task Scheduler

An AI-powered daily planner that utilizes the Claude API to help you manage your day effectively. It analyzes your goals, weekly schedule, recent sleep/energy logs, and task-completion history to generate a realistic daily plan. It also features a coach-style chat that retains memory of your entire history for continuous support and check-ins.

## Stack

- Java 17
- Spring Boot 4.0.4
- Spring Data JPA
- H2 Database (Development) / PostgreSQL (Production)
- Claude API

## Project Layout

```
model/        JPA entities (Task, SubTask, UserProfile, DailyLog, ChatMessage)
repository/   Spring Data interfaces
service/      Business logic + AiPlanningService (talks to Claude)
controller/   REST endpoints
dto/          Request/response shapes for the chat & plan endpoints
config/       WebClient bean for the Claude API
resources/
  static/index.html             Frontend UI (served by Spring Boot)
  application.properties        Development config (H2)
  application-prod.properties   Production config (Postgres)
```

## Running Locally

### Prerequisites

- Java 17 or higher
- A Claude API key from the [Anthropic Console](https://console.anthropic.com)

### Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd task-scheduler
   ```

2. Configure environment variables:
   Copy the example environment file and add your Claude API key.
   ```bash
   cp .env.example .env
   ```
   
   Load the environment variable:
   * **macOS/Linux:**
     ```bash
     export $(cat .env | xargs)
     ```
   * **Windows (PowerShell):**
     ```powershell
     $env:CLAUDE_API_KEY = "your-api-key-here"
     ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(On Windows, use `.\mvnw.cmd spring-boot:run`)*

The application will start on **http://localhost:8080**. The frontend is served directly from `resources/static/index.html`. 

The local development environment uses an H2 file-based database located at `./data/taskschedulerdb.mv.db`, ensuring your tasks and chat history persist across restarts. You can access the H2 console at **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:file:./data/taskschedulerdb`, Username: `sa`, Password: `[blank]`).

### Basic Usage Flow
1. Fill in the **Profile** card (goals, weekly schedule, baseline sleep) and save it.
2. Log today's sleep and energy metrics in the **Today's log** card.
3. Click **Generate plan** to invoke the AI planner to create today's tasks and subtasks.
4. Update subtask completion using the sliders on each subtask.
5. Use the **Check in** chatbox for personalized coaching based on your activity and history.

## API Reference

You can interact with the API directly without the frontend:

```bash
# Profile
curl http://localhost:8080/api/profile
curl -X PUT http://localhost:8080/api/profile \
  -H "Content-Type: application/json" \
  -d '{"goals":"finish thesis draft","weeklySchedule":"classes 9am-2pm Mon/Wed/Fri","baselineSleep":7.5}'

# Daily log
curl -X POST http://localhost:8080/api/log \
  -H "Content-Type: application/json" \
  -d '{"date":"2026-07-18","nightSleepHrs":6.5,"afternoonSleepHrs":0,"morningEnergy":3,"eveningEnergy":2,"notes":"rough night"}'
curl http://localhost:8080/api/log/history

# Plan
curl -X POST http://localhost:8080/api/plan/generate
curl http://localhost:8080/api/plan/today
curl -X PATCH http://localhost:8080/api/subtask/1 -H "Content-Type: application/json" -d '{"completed":70}'

# Chat
curl -X POST http://localhost:8080/api/chat -H "Content-Type: application/json" -d '{"message":"I skipped my study block again today"}'
curl http://localhost:8080/api/chat/history
```

## Deployment

To deploy the application for production, you will need a PostgreSQL database and a hosting provider that supports Java Spring Boot applications.

Ensure the following environment variables are set in your production environment:

| Variable | Description |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` to activate production configurations |
| `DATABASE_URL` | PostgreSQL connection string (e.g., `jdbc:postgresql://<host>:<port>/<db>`) |
| `DATABASE_USERNAME` | Database username |
| `DATABASE_PASSWORD` | Database password |
| `CLAUDE_API_KEY` | Your Anthropic API key |

The frontend is packaged and served by the Spring Boot application, requiring no separate deployment process.