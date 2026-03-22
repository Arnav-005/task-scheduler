package com.arnav.taskscheduler.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity //tells JPA to create a database for this model
@Table(name = "DailyLogs") //gives table name
public class DailyLog  {
    @Id //table key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //increment id
    private Long id;

    //no arg constructor for JPA to instantiate the class
    public DailyLog() {}

    //fields:-

    //attributes:
    private LocalDate date;
    private float nightSleepHrs;
    private float afternoonSleepHrs;
    private int morningEnergy; //1-5
    private int eveningEnergy;
    private String notes; //optional

    //getters:
    public Long getId(){return id;}
    public LocalDate getDate(){return date;}
    public float getNightSleepHrs(){return nightSleepHrs;}
    public float getAfternoonSleepHrs(){return afternoonSleepHrs;}
    public int getMorningEnergy(){return morningEnergy;}
    public int getEveningEnergy(){return eveningEnergy;}
    public String getNotes(){return notes;}

    //setters:
    public void setDate(LocalDate date){this.date = date;}
    public void setNightSleepHrs(float hrs){nightSleepHrs = hrs;}
    public void setAfternoonSleepHrs(float hrs){afternoonSleepHrs = hrs;}
    public void setMorningEnergy(int energyLevel){morningEnergy = energyLevel;}
    public void setEveningEnergy(int energyLevel){eveningEnergy = energyLevel;}
    public void setNotes(String notes){this.notes = notes;}
}
