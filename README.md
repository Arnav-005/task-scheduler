# Task Scheduler

AI-powered daily planner: it looks at your goals, weekly schedule, recent sleep/energy
logs, and recent task-completion history, then asks Claude to generate a realistic plan
for the day - and lets you check in with a coach-style chat that has memory of your
whole history, not just the current message.

## Stack

Java 17 · Spring Boot 4.0.4 · Spring Data JPA · H2 (dev) / PostgreSQL (prod) · Claude API

## Project layout

```
model/        JPA entities (Task, SubTask, UserProfile, DailyLog, ChatMessage)
repository/   Spring Data interfaces
service/      Business logic + AiPlanningService (talks to Claude)
controller/   REST endpoints
dto/          Request/response shapes for the chat & plan endpoints
config/       WebClient bean for the Claude API
resources/
  static/index.html     the whole frontend (served by Spring Boot itself)
  application.properties        dev config (H2)
  application-prod.properties   prod config (Postgres, activated via a Spring profile)
```

## 1. Run it locally

You need Java 17+ and a Claude API key from the [Anthropic Console](https://console.anthropic.com).

```bash
git clone <your repo url>
cd task-scheduler
cp .env.example .env      # then edit .env and paste in your real key
export $(cat .env | xargs)  # loads CLAUDE_API_KEY into your shell (macOS/Linux)
./mvnw spring-boot:run
```

On Windows (PowerShell):
```powershell
$env:CLAUDE_API_KEY = "sk-ant-your-key-here"
.\mvnw.cmd spring-boot:run
```

The app starts on **http://localhost:8080** - open that URL and you'll see the frontend
directly (it's served from `resources/static/index.html`, no separate frontend process
needed). Your H2 database is a file at `./data/taskschedulerdb.mv.db`, so your tasks and
chat history survive restarts. Browse it directly at **http://localhost:8080/h2-console**
(JDBC URL: `jdbc:h2:file:./data/taskschedulerdb`, username `sa`, blank password).

### First run walkthrough
1. Fill in the **Profile** card (goals, weekly schedule, baseline sleep) and save it.
2. Optionally log today's sleep/energy in the **Today's log** card.
3. Click **Generate plan** - this calls Claude and creates today's tasks + subtasks.
4. Drag the sliders on each subtask to update completion (`PATCH /api/subtask/{id}`).
5. Use the **Check in** chatbox at the bottom - it has full memory of prior messages
   plus your logs and task history, so it can call out real patterns.

## 2. Test the API directly (no frontend)

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

## 3. Deploy

**Database:** create a free Postgres instance on [Neon](https://neon.tech) or
[Railway](https://railway.app) - either gives you a connection string in under 10 minutes.

**Backend:** deploy this repo to [Railway](https://railway.app) or
[Render](https://render.com) (both auto-detect a Maven/Spring Boot app, or you can add a
`Dockerfile` if you want more control). Set these environment variables on the host:

| Variable | Value |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DATABASE_URL` | `jdbc:postgresql://<host>:<port>/<db>` from your Postgres provider |
| `DATABASE_USERNAME` | from your Postgres provider |
| `DATABASE_PASSWORD` | from your Postgres provider |
| `CLAUDE_API_KEY` | your real Anthropic API key |

**Frontend:** nothing extra to do - `index.html` is served by the same Spring Boot app,
so one deploy gives you the whole thing at one URL.

## Known model quirks worth knowing before an interview

- `Task.setDone()` (marks `status = true`) has no way to un-set it, and nothing in this
  build currently calls it - task completion for the UI is derived purely from subtask
  percentages (`TaskService.isTaskFullyDone`). Worth deciding if you want `status` to mean
  something distinct from subtask completion, or to drop it.
- The single-user profile assumes the first `UserProfile` row ends up with `id=1`. Fine
  for a single-user demo app; would need a real user/session model to scale.
