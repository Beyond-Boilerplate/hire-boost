package com.sardul3.hireboost.autoview.service;

import com.sardul3.hireboost.temporal.config.TemporalConfigProperties;
import com.sardul3.hireboost.temporal.config.WorkflowIdGenerator;
import com.sardul3.hireboost.autoview.workflow.LinkedInProfileViewWorkflow;
import com.sardul3.hireboost.autoview.model.UserCredentials;
import org.springframework.stereotype.Service;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import static com.sardul3.hireboost.temporal.config.TemporalConstants.Workflows.LI_PROFILE_VISIT_WORKFLOW;

@Service
@RequiredArgsConstructor
public class LinkedInProfileViewService {

    private final WorkflowClient workflowClient;
    private final TemporalConfigProperties workflowConfig;

    /**
     * Triggers the LinkedIn profile visit workflow.
     *
     * @param credentials User credentials for login.
     */
    public void startProfileVisitWorkflow(UserCredentials credentials) {
        String workflowId = WorkflowIdGenerator.generateWorkflowId(LI_PROFILE_VISIT_WORKFLOW, workflowConfig);

        LinkedInProfileViewWorkflow workflow = createWorkflowStub(workflowId);
        WorkflowClient.execute(workflow::visitProfiles, credentials.getUsername(), credentials.getPassword());
    }

    /**
     * Creates a new Temporal workflow stub.
     *
     * @return Workflow stub instance.
     */
    private LinkedInProfileViewWorkflow createWorkflowStub(String workflowId) {
        String currentVersion = workflowConfig.getCurrentVersions().getWorkflows()
                .get(LI_PROFILE_VISIT_WORKFLOW);

        String taskQueue = workflowConfig.getWorkflows()
                .get(LI_PROFILE_VISIT_WORKFLOW)
                .getVersions().get(currentVersion)
                .getTaskQueue();

        return workflowClient.newWorkflowStub(
                LinkedInProfileViewWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(taskQueue)
                        .setWorkflowId(workflowId)
                        .setWorkflowExecutionTimeout(Duration.ofMinutes(10))
                        .build()
        );
    }
}

