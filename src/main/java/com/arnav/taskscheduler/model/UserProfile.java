package com.arnav.taskscheduler.model;

import jakarta.persistence.*;

@Entity //tells JPA to create a database for this class
@Table(name = "UserProfiles")
public class UserProfile {
    @Id //table key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //increment ID
    private Long id;

    //constructor for JPA to instantiate the class
    public UserProfile() {}

    //fields:-

    //attributes:
    @Column(columnDefinition = "TEXT")
    private String goals;

    @Column(columnDefinition = "TEXT")
    private String weeklySchedule;
    private float baselineSleep;

    //getters:
    public Long getId(){return id;}
    public String getGoals(){return goals;}
    public String getWeeklySchedule(){return weeklySchedule;}
    public float getBaselineSleep(){return baselineSleep;}

    //setters:
    public void setGoals(String goals){this.goals = goals;}
    public void setWeeklySchedule(String schedule){weeklySchedule = schedule;}
    public void setBaselineSleep(float sleep){baselineSleep = sleep;}
}
