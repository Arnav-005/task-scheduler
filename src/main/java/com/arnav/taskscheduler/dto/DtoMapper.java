package com.arnav.taskscheduler.dto;

import com.arnav.taskscheduler.model.ChatMessage;
import com.arnav.taskscheduler.model.SubTask;
import com.arnav.taskscheduler.model.Task;
import com.arnav.taskscheduler.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

public final class DtoMapper {

    private DtoMapper() {}

    public static SubTaskDto toDto(SubTask subTask) {
        return new SubTaskDto(subTask.getId(), subTask.getTitle(), subTask.getCompleted());
    }

    public static TaskDto toDto(Task task, TaskService taskService) {
        List<SubTaskDto> subTaskDtos = task.getSubTasks().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDate(),
                task.getParentGoal(),
                task.isAiGenerated(),
                taskService.isTaskFullyDone(task),
                subTaskDtos
        );
    }

    public static List<TaskDto> toDtoList(List<Task> tasks, TaskService taskService) {
        return tasks.stream().map(t -> toDto(t, taskService)).collect(Collectors.toList());
    }

    public static ChatMessageDto toDto(ChatMessage message) {
        return new ChatMessageDto(message.getId(), message.getRole(), message.getContent(), message.getTimestamp());
    }

    public static List<ChatMessageDto> toChatDtoList(List<ChatMessage> messages) {
        return messages.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }
}
