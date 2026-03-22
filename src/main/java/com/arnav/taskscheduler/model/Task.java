package com.arnav.taskscheduler.model;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity   //tells JPA to create a database table for this class
@Table(name = "Tasks") //gives table name
public class Task {
    @Id  //table key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //increment ID
    private Long id;

    //constructor for JPA to instantiate the class
    public Task() {}

    //setting up bidirectional relation with subTask
    @JsonManagedReference //used on parent side to prevent infinite fetching in REST APIs
    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTask> subTasks = new ArrayList<>();

    //helper methods
    public void addSubTask(SubTask subTask){
        subTasks.add(subTask);
        subTask.setParentTask(this); //sets foreign key for this subTask
    }
    public void removeSubTask(SubTask subTask){
        subTasks.remove(subTask);
        subTask.setParentTask(null); //set up child for orphan removal
    }

    //fields:-

    //attributes:
    private boolean status;
    private String title;
    private LocalDate taskDate;
    private String parentGoal;
    private boolean aiGenerated;

    //getters:
    public List<SubTask> getSubTasks(){return subTasks;}
    public Long getId() {return id;}
    public boolean isDone() {return status;}
    public String getTitle() {return title;}
    public LocalDate getDate() {return taskDate;}
    public String getParentGoal() {return parentGoal;}
    public boolean isAiGenerated() {return aiGenerated;}

    //setters:
    public void setSubTasks(List<SubTask> subTasks){this.subTasks = subTasks;}
    public void setTitle(String text){ title = text; }
    public void setDone(){ status = true; }
    public void setDate(LocalDate taskDate){ this.taskDate = taskDate; }
    public void setParentGoal(String parentGoal){ this.parentGoal = parentGoal; }
    public void setAiGenerated(){ aiGenerated = true; }
}
