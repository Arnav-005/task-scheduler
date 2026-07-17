package com.arnav.taskscheduler.dto;

// Body for PATCH /api/subtask/{id}: { "completed": 70 }
public record SubTaskCompletionRequestDto(int completed) {}
