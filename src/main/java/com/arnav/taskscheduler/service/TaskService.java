package com.arnav.taskscheduler.service;

import com.arnav.taskscheduler.model.SubTask;
import com.arnav.taskscheduler.model.Task;
import com.arnav.taskscheduler.repository.SubTaskRepository;
import com.arnav.taskscheduler.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;

    public TaskService(TaskRepository taskRepository, SubTaskRepository subTaskRepository) {
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
    }

    public List<Task> getTasksForDate(LocalDate date) {
        return taskRepository.findByTaskDate(date);
    }

    public List<Task> getTasksBetween(LocalDate start, LocalDate end) {
        return taskRepository.findByTaskDateBetween(start, end);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Uses Task's own addSubTask() helper so the bidirectional
    // parentTask/subTasks link is always set correctly on both sides.
    public Task addSubTasksToTask(Long taskId, List<SubTask> subtasks) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        for (SubTask subTask : subtasks) {
            task.addSubTask(subTask);
        }
        return taskRepository.save(task);
    }

    public SubTask updateSubTaskCompletion(Long subTaskId, int percent) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new IllegalArgumentException("SubTask not found: " + subTaskId));
        int clamped = Math.max(0, Math.min(100, percent));
        subTask.setCompleted(clamped);
        return subTaskRepository.save(subTask);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    // Derived, not stored: a task counts as fully done only once every
    // subtask under it is at 100%. A task with no subtasks is not "done".
    public boolean isTaskFullyDone(Task task) {
        if (task.getSubTasks() == null || task.getSubTasks().isEmpty()) {
            return false;
        }
        return task.getSubTasks().stream().allMatch(s -> s.getCompleted() == 100);
    }
}
