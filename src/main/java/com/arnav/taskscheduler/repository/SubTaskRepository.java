package com.arnav.taskscheduler.repository; //reflecting the folder structure we are in
import com.arnav.taskscheduler.model.SubTask; //importing the SubTask model

//necessary imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
    //now we automatically have save, deleteById, findById, etc.
}