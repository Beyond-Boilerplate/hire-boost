package com.sardul3.hireboost.autopost.schedule;

import com.sardul3.hireboost.autopost.temporal.workflow.PostGenerateAndPublishWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PostGenerationScheduler {

    private final WorkflowClient workflowClient;

    public PostGenerationScheduler(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @Scheduled(fixedRate = 10000)
    public void schedulePostGeneration() {
        String candidateId = "auto-scheduled-candidate";

        PostGenerateAndPublishWorkflow workflow = workflowClient.newWorkflowStub(
                PostGenerateAndPublishWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("LinkedInPostQueue")
                        .build()
        );

        WorkflowClient.start(workflow::generateAndPost);
        System.out.println("🔄 Scheduled post generation started for candidate: " + candidateId);
    }
}

