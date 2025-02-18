package com.sardul3.hireboost.autopost.service;


import com.sardul3.hireboost.temporal.config.TemporalConfigProperties;
import com.sardul3.hireboost.temporal.config.TemporalConstants;
import com.sardul3.hireboost.autopost.workflow.PostGenerateAndPublishWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LinkedInPostService {

    private final WorkflowClient workflowClient;
    private final TemporalConfigProperties workflowConfig;

    /**
     * Triggers the LinkedIn post generation workflow.
     *
     * @param candidateId The candidate ID.
     * @return A confirmation message.
     */
    public String startLinkedInPostWorkflow(String candidateId) {
        PostGenerateAndPublishWorkflow workflow = createWorkflowStub();
        WorkflowClient.start(workflow::generateAndPost);
        return "LinkedIn post generation workflow started for candidate: " + candidateId;
    }

    /**
     * Creates a new Temporal workflow stub.
     *
     * @return Workflow stub instance.
     */
    private PostGenerateAndPublishWorkflow createWorkflowStub() {
        String currentVersion = workflowConfig.getCurrentVersions().getWorkflows()
                .get(TemporalConstants.Workflows.LI_POST_WORKFLOW);

        String taskQueue = workflowConfig.getWorkflows()
                .get(TemporalConstants.Workflows.LI_POST_WORKFLOW)
                .getVersions().get(currentVersion)
                .getTaskQueue();

        return workflowClient.newWorkflowStub(
                PostGenerateAndPublishWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(taskQueue)
                        .build()
        );
    }
}

