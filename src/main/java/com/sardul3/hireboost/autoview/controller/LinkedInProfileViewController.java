package com.sardul3.hireboost.autoview.controller;

import com.sardul3.hireboost.autoview.model.UserCredentials;
import com.sardul3.hireboost.autoview.service.LinkedInProfileViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/li/profile")
@RequiredArgsConstructor
public class LinkedInProfileViewController {


    private final LinkedInProfileViewService linkedInProfileViewService;

    @PostMapping("/visit")
    public ResponseEntity<String> startScraper(@RequestBody UserCredentials credentials) {
        linkedInProfileViewService.startProfileVisitWorkflow(credentials);
        return ResponseEntity.ok("LinkedIn profile visit workflow submitted.");
    }
}
