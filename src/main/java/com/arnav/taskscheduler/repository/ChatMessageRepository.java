package com.arnav.taskscheduler.repository; //reflecting the current folder path
import com.arnav.taskscheduler.model.ChatMessage; //importing the ChatMessage model

//necessary imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    //now we automatically have save, deleteById, findById etc.
}
