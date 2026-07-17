package com.arnav.taskscheduler.repository; //reflecting the current folder path
import com.arnav.taskscheduler.model.DailyLog; //importing the DailyLog model

//necessary imports
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    //now we automatically have save, addById, deleteById

    //one custom method is needed
    List<DailyLog> findByDate(LocalDate date);

    //used by DailyLogService.getLast7Days() / AiPlanningService context building
    //Spring Data parses "Top7...OrderByDateDesc" into a LIMIT 7 query sorted by date descending
    List<DailyLog> findTop7ByOrderByDateDesc();

    //all interface methods are public by default, so access modifiers are redundant
    //JPA figures out the SQL query for findBy<attribute> itself if <attribute> exists inside the model
}