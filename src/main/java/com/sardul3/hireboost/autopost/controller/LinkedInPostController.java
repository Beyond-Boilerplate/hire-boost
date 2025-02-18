package com.sardul3.hireboost.autopost.controller;

import com.sardul3.hireboost.autopost.service.LinkedInPostService;
import io.temporal.client.WorkflowClient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/li/post")
public class LinkedInPostController {
    private final WorkflowClient workflowClient;

    private final LinkedInPostService linkedInPostService;

    @PostMapping("/generate/{candidateId}")
    public String triggerPostGeneration(@PathVariable String candidateId) {
        return linkedInPostService.startLinkedInPostWorkflow(candidateId);
    }

}
