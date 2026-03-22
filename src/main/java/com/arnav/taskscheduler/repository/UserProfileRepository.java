package com.arnav.taskscheduler.repository; //reflecting the current folder path
import com.arnav.taskscheduler.model.UserProfile; //importing the UserProfile model

//necessary imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    //now we automatically have save, deleteById, findByID, etc.
}