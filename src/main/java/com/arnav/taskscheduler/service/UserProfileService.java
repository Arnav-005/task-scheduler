package com.arnav.taskscheduler.service;

import com.arnav.taskscheduler.model.UserProfile;
import com.arnav.taskscheduler.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    // This app is single-user: the profile always lives at row id=1.
    // If nothing has been saved yet, create a sensible default so the
    // rest of the app (and the AI context builder) always has something to read.
    public UserProfile getProfile() {
        return userProfileRepository.findById(1L).orElseGet(this::createDefaultProfile);
    }

    public UserProfile updateProfile(UserProfile updated) {
        UserProfile profile = getProfile();
        profile.setGoals(updated.getGoals());
        profile.setWeeklySchedule(updated.getWeeklySchedule());
        profile.setBaselineSleep(updated.getBaselineSleep());
        return userProfileRepository.save(profile);
    }

    private UserProfile createDefaultProfile() {
        UserProfile profile = new UserProfile();
        profile.setGoals("");
        profile.setWeeklySchedule("");
        profile.setBaselineSleep(7.5f);
        return userProfileRepository.save(profile);
    }
}
