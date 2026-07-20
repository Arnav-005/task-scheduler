package com.arnav.taskscheduler.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity //tells the JPA to create a database table for this class
@Table(name = "ChatMessages") //gives table name
public class ChatMessage {
    @Id //table key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //increment ID
    private Long id;

    //no arg constructor for JPA to instantiate the class
    public ChatMessage() {}

    //fields:-

    //attributes:
    private LocalDateTime timestamp;
    private String role;  //user or assistant
    @Column(columnDefinition = "TEXT")
    private String content;

    //getters:
    public Long getId(){return id;}
    public LocalDateTime getTimestamp(){return timestamp;}
    public String getRole(){return role;}
    public String getContent(){return content;}

    //setters:
    public void setTimestamp(LocalDateTime timestamp){this.timestamp = timestamp;}
    public void setRole(String role){this.role = role;}
    public void setContent(String content){this.content = content;}
}
