package com.arnav.taskscheduler.controller;

import com.arnav.taskscheduler.model.UserProfile;
import com.arnav.taskscheduler.service.UserProfileService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin
public class ProfileController {

    private final UserProfileService userProfileService;

    public ProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public UserProfile getProfile() {
        return userProfileService.getProfile();
    }

    @PutMapping
    public UserProfile updateProfile(@RequestBody UserProfile updated) {
        return userProfileService.updateProfile(updated);
    }
}
