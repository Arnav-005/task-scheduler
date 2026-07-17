package com.arnav.taskscheduler.service;

import com.arnav.taskscheduler.model.DailyLog;
import com.arnav.taskscheduler.repository.DailyLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;

    public DailyLogService(DailyLogRepository dailyLogRepository) {
        this.dailyLogRepository = dailyLogRepository;
    }

    // Upsert by date: only one log per calendar day makes sense, so if a
    // log already exists for this date we update it instead of duplicating.
    public DailyLog saveLog(DailyLog log) {
        List<DailyLog> existing = dailyLogRepository.findByDate(log.getDate());
        if (!existing.isEmpty()) {
            DailyLog toUpdate = existing.get(0);
            toUpdate.setNightSleepHrs(log.getNightSleepHrs());
            toUpdate.setAfternoonSleepHrs(log.getAfternoonSleepHrs());
            toUpdate.setMorningEnergy(log.getMorningEnergy());
            toUpdate.setEveningEnergy(log.getEveningEnergy());
            toUpdate.setNotes(log.getNotes());
            return dailyLogRepository.save(toUpdate);
        }
        return dailyLogRepository.save(log);
    }

    public List<DailyLog> getLast7Days() {
        return dailyLogRepository.findTop7ByOrderByDateDesc();
    }

    public Optional<DailyLog> getLogByDate(LocalDate date) {
        List<DailyLog> logs = dailyLogRepository.findByDate(date);
        return logs.isEmpty() ? Optional.empty() : Optional.of(logs.get(0));
    }
}
