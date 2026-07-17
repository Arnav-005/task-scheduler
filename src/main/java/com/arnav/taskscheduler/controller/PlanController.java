package com.arnav.taskscheduler.controller;

import com.arnav.taskscheduler.dto.DtoMapper;
import com.arnav.taskscheduler.dto.SubTaskCompletionRequestDto;
import com.arnav.taskscheduler.dto.SubTaskDto;
import com.arnav.taskscheduler.dto.TaskDto;
import com.arnav.taskscheduler.model.SubTask;
import com.arnav.taskscheduler.model.Task;
import com.arnav.taskscheduler.service.AiPlanningException;
import com.arnav.taskscheduler.service.AiPlanningService;
import com.arnav.taskscheduler.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class PlanController {

    private final TaskService taskService;
    private final AiPlanningService aiPlanningService;

    public PlanController(TaskService taskService, AiPlanningService aiPlanningService) {
        this.taskService = taskService;
        this.aiPlanningService = aiPlanningService;
    }

    @GetMapping("/plan/today")
    public List<TaskDto> getToday() {
        List<Task> tasks = taskService.getTasksForDate(LocalDate.now());
        return DtoMapper.toDtoList(tasks, taskService);
    }

    @PostMapping("/plan/generate")
    public ResponseEntity<?> generatePlan() {
        try {
            List<Task> created = aiPlanningService.generateDailyPlan(LocalDate.now());
            return ResponseEntity.ok(DtoMapper.toDtoList(created, taskService));
        } catch (AiPlanningException e) {
            // Basic failure handling: the AI call/parse failed, return a clean
            // 502 instead of leaking a stack trace to the frontend.
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Plan generation failed", "message", e.getMessage()));
        }
    }

    @PatchMapping("/subtask/{id}")
    public SubTaskDto updateSubtask(@PathVariable Long id, @RequestBody SubTaskCompletionRequestDto body) {
        SubTask updated = taskService.updateSubTaskCompletion(id, body.completed());
        return DtoMapper.toDto(updated);
    }
}
