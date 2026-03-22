package com.arnav.taskscheduler.repository; //folder path or structure we are in
import com.arnav.taskscheduler.model.Task; //importing the Task model

//necessary imports
import java.util.*;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    //now we automatically have save, findById, deleteById, findAll etc.

    //one custom method for fetching tasks by date
    List<Task> findByTaskDate(LocalDate date);
}

//NOTE:
//Long with an L can be both long and null.
//if null, JPA knows to insert into DB. if long, JPA knows to update DB