package com.arnav.taskscheduler.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity //tells JPA to create a database for this model
@Table(name = "SubTasks") //gives table name
public class SubTask {
    @Id //table key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //increment ID
    private Long id;

    //no arg constructor for JPA to instantiate the class
    public SubTask() {}

    //fields:-

    //attributes:
    private String title;
    private int completed; //int for percentage

    //annotations for a particular field are written above it:
    @JsonBackReference //used on child side to prevent infinite fetching loop in REST APIs
    @ManyToOne //many SubTasks can belong to one Task
    //name the foreign key from parent table
    //the child table can have multiple rows/subtasks for this task_id
    @JoinColumn(name = "task_id") //default name but explicitly stated it
    private Task parentTask;

    //getters:
    public long getId(){return id;}
    public String getTitle(){return title;}
    public int getCompleted(){return completed;}
    public Task getParentTask(){return parentTask;}

    //setters;
    public void setTitle(String title){this.title = title;}
    public void setCompleted(int percent){completed = percent;}
    public void setParentTask(Task parent){parentTask = parent;}
}
