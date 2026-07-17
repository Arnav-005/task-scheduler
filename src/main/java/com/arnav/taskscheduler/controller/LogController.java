package com.arnav.taskscheduler.controller;

import com.arnav.taskscheduler.model.DailyLog;
import com.arnav.taskscheduler.service.DailyLogService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/log")
@CrossOrigin
public class LogController {

    private final DailyLogService dailyLogService;

    public LogController(DailyLogService dailyLogService) {
        this.dailyLogService = dailyLogService;
    }

    @PostMapping
    public DailyLog saveLog(@RequestBody DailyLog log) {
        return dailyLogService.saveLog(log);
    }

    @GetMapping("/history")
    public List<DailyLog> getHistory() {
        return dailyLogService.getLast7Days();
    }
}
