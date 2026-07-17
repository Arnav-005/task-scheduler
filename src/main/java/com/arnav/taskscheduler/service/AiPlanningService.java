package com.arnav.taskscheduler.service;

import com.arnav.taskscheduler.model.ChatMessage;
import com.arnav.taskscheduler.model.DailyLog;
import com.arnav.taskscheduler.model.SubTask;
import com.arnav.taskscheduler.model.Task;
import com.arnav.taskscheduler.model.UserProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiPlanningService {

    // --- System prompts, verbatim from the blueprint (Section 4) ---

    private static final String PLAN_SYSTEM_PROMPT = """
            You are a planning assistant for a college student. You generate a realistic,
            achievable daily task list based on their goals, weekly schedule, recent sleep
            and energy patterns, and recent task completion history.

            Rules:
            - Break each task into 2-4 concrete subtasks.
            - If sleep hours over the last 3 days are below baseline, reduce workload and
              say why in a short note.
            - If recent subtask completion is consistently low (<50%), scale down scope
              rather than repeating the same failed task size.
            - Respect the day's schedule block (college hours) - do not assign heavy tasks
              during college hours.
            - Output ONLY valid JSON matching this schema, no prose:
            {
              "tasks": [
                {
                  "title": "string",
                  "parentGoal": "string",
                  "subtasks": ["string", "string"]
                }
              ],
              "note": "one short sentence explaining any adjustment made"
            }
            """;

    private static final String CHAT_SYSTEM_PROMPT = """
            You are a supportive but honest planning coach for a college student. You have
            access to their goals, schedule, sleep/energy logs, task completion history,
            and full conversation history.

            When the user shares context (a schedule change, a bad day, a slip in
            consistency), do two things:
            1. Acknowledge briefly and concretely - reference their actual data, not
               generic sympathy.
            2. If you detect a pattern (procrastination, over-documentation, pace drop,
               responsibility-shifting, tardiness, mental absence, avoidance via
               flexibility, burnout, overwhelm, anxiety, disengagement), name it plainly
               and offer ONE relevant framework or mindset shift. Keep it short - 3-5
               sentences max. Do not lecture.

            Do not diagnose clinically. Do not moralize. Be direct, not soft.
            """;

    private final WebClient claudeWebClient;
    private final ObjectMapper objectMapper;
    private final UserProfileService userProfileService;
    private final DailyLogService dailyLogService;
    private final TaskService taskService;
    private final ChatService chatService;

    @Value("${claude.model}")
    private String model;

    public AiPlanningService(WebClient claudeWebClient,
                              ObjectMapper objectMapper,
                              UserProfileService userProfileService,
                              DailyLogService dailyLogService,
                              TaskService taskService,
                              ChatService chatService) {
        this.claudeWebClient = claudeWebClient;
        this.objectMapper = objectMapper;
        this.userProfileService = userProfileService;
        this.dailyLogService = dailyLogService;
        this.taskService = taskService;
        this.chatService = chatService;
    }

    /**
     * Gathers context, asks Claude for a JSON-only plan, parses it into
     * Task + SubTask entities (aiGenerated = true), saves them, and returns them.
     */
    public List<Task> generateDailyPlan(LocalDate date) {
        ObjectNode context = buildContextJson(false);

        String userPrompt = "Date to plan for: " + date + " (" + date.getDayOfWeek() + ")\n\n"
                + "Context:\n" + context.toString();

        String responseText = callClaude(PLAN_SYSTEM_PROMPT, userPrompt);

        JsonNode planJson;
        try {
            planJson = extractJson(responseText);
        } catch (Exception e) {
            throw new AiPlanningException("Claude's plan response wasn't valid JSON: " + e.getMessage(), e);
        }

        List<Task> createdTasks = new ArrayList<>();
        JsonNode tasksNode = planJson.path("tasks");
        if (tasksNode.isArray()) {
            for (JsonNode taskNode : tasksNode) {
                Task task = new Task();
                task.setTitle(taskNode.path("title").asText("Untitled task"));
                task.setParentGoal(taskNode.path("parentGoal").asText(""));
                task.setDate(date);
                task.setAiGenerated();

                Task saved = taskService.createTask(task);

                List<SubTask> subTasks = new ArrayList<>();
                JsonNode subtasksNode = taskNode.path("subtasks");
                if (subtasksNode.isArray()) {
                    for (JsonNode subtaskTitle : subtasksNode) {
                        SubTask subTask = new SubTask();
                        subTask.setTitle(subtaskTitle.asText());
                        subTask.setCompleted(0);
                        subTasks.add(subTask);
                    }
                }
                if (!subTasks.isEmpty()) {
                    saved = taskService.addSubTasksToTask(saved.getId(), subTasks);
                }
                createdTasks.add(saved);
            }
        }
        return createdTasks;
    }

    /**
     * Saves the user's message, sends full context (including chat history) to
     * Claude for a behavioral-pattern-aware reply, saves and returns that reply.
     * Falls back to a friendly message instead of throwing if the API call fails,
     * since a chat endpoint should always respond conversationally.
     */
    public String respondToChat(String userMessage) {
        chatService.saveMessage("user", userMessage);

        ObjectNode context = buildContextJson(true);
        String userPrompt = "New message from the user:\n" + userMessage
                + "\n\nContext (profile, recent sleep/energy logs, recent task completion, "
                + "and full prior chat history under \"chatHistory\"):\n" + context.toString();

        String reply;
        try {
            reply = callClaude(CHAT_SYSTEM_PROMPT, userPrompt);
            if (reply.isBlank()) {
                reply = "I didn't get a usable response back just now - mind trying that again?";
            }
        } catch (Exception e) {
            reply = "I couldn't reach the planning model just now (" + e.getMessage()
                    + "). Please try again in a moment.";
        }

        chatService.saveMessage("assistant", reply);
        return reply;
    }

    // --- Context building ---

    private ObjectNode buildContextJson(boolean includeChatHistory) {
        ObjectNode root = objectMapper.createObjectNode();

        UserProfile profile = userProfileService.getProfile();
        ObjectNode profileNode = root.putObject("profile");
        profileNode.put("goals", nullToEmpty(profile.getGoals()));
        profileNode.put("weeklySchedule", nullToEmpty(profile.getWeeklySchedule()));
        profileNode.put("baselineSleep", profile.getBaselineSleep());

        ArrayNode logsNode = root.putArray("recentLogs");
        for (DailyLog log : dailyLogService.getLast7Days()) {
            ObjectNode logNode = logsNode.addObject();
            logNode.put("date", log.getDate() != null ? log.getDate().toString() : "");
            logNode.put("nightSleepHrs", log.getNightSleepHrs());
            logNode.put("afternoonSleepHrs", log.getAfternoonSleepHrs());
            logNode.put("morningEnergy", log.getMorningEnergy());
            logNode.put("eveningEnergy", log.getEveningEnergy());
            logNode.put("notes", nullToEmpty(log.getNotes()));
        }

        ArrayNode completionNode = root.putArray("recentTaskCompletion");
        LocalDate today = LocalDate.now();
        List<Task> recentTasks = taskService.getTasksBetween(today.minusDays(5), today);
        for (Task task : recentTasks) {
            ObjectNode taskNode = completionNode.addObject();
            taskNode.put("date", task.getDate() != null ? task.getDate().toString() : "");
            taskNode.put("title", task.getTitle());
            taskNode.put("subtaskCompletionAvg", averageCompletion(task));
        }

        if (includeChatHistory) {
            ArrayNode chatNode = root.putArray("chatHistory");
            for (ChatMessage message : chatService.getHistory()) {
                ObjectNode messageNode = chatNode.addObject();
                messageNode.put("role", message.getRole());
                messageNode.put("content", message.getContent());
            }
        }

        return root;
    }

    private double averageCompletion(Task task) {
        if (task.getSubTasks() == null || task.getSubTasks().isEmpty()) {
            return 0.0;
        }
        return task.getSubTasks().stream().mapToInt(SubTask::getCompleted).average().orElse(0.0);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    // --- Claude API call ---

    private String callClaude(String systemPrompt, String userMessage) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", model);
        body.put("max_tokens", 2048);
        body.put("system", systemPrompt);

        ArrayNode messages = body.putArray("messages");
        ObjectNode userMsg = messages.addObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);

        JsonNode response;
        try {
            response = claudeWebClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (Exception e) {
            throw new AiPlanningException("Claude API call failed: " + e.getMessage(), e);
        }

        if (response == null) {
            throw new AiPlanningException("Empty response from Claude API");
        }
        if (response.has("error")) {
            throw new AiPlanningException("Claude API returned an error: "
                    + response.path("error").path("message").asText());
        }

        StringBuilder sb = new StringBuilder();
        JsonNode contentArray = response.path("content");
        if (contentArray.isArray()) {
            for (JsonNode block : contentArray) {
                if ("text".equals(block.path("type").asText())) {
                    sb.append(block.path("text").asText());
                }
            }
        }
        return sb.toString().trim();
    }

    // Strips accidental markdown code fences before parsing, in case the
    // model wraps its JSON in ```json ... ``` despite being told not to.
    private JsonNode extractJson(String text) throws Exception {
        String cleaned = text.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```(json)?", "").trim();
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
            }
        }
        return objectMapper.readTree(cleaned);
    }
}
