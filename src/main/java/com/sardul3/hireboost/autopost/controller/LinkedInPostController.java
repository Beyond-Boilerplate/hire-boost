package com.sardul3.hireboost.autopost.controller;

import com.sardul3.hireboost.autopost.temporal.workflow.PostGenerateAndPublishWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
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

    @PostMapping("/generate")
    public String triggerPostGeneration(@PathVariable String candidateId) {
        PostGenerateAndPublishWorkflow workflow = workflowClient.newWorkflowStub(
                PostGenerateAndPublishWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("LinkedInPostQueue")
                        .build()
        );

        WorkflowClient.start(workflow::generateAndPost);
        return "Post generation workflow started";
    }

}
