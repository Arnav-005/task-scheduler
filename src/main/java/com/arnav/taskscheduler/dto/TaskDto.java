package com.arnav.taskscheduler.dto;

import java.time.LocalDate;
import java.util.List;

public record TaskDto(
        Long id,
        String title,
        LocalDate taskDate,
        String parentGoal,
        boolean aiGenerated,
        boolean fullyDone,
        List<SubTaskDto> subTasks
) {}
