package com.sardul3.hireboost.autopost.schedule;

import com.sardul3.hireboost.autopost.workflow.PostGenerateAndPublishWorkflow;
import com.sardul3.hireboost.temporal.config.TemporalConfigProperties;
import com.sardul3.hireboost.temporal.config.WorkflowIdGenerator;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.sardul3.hireboost.temporal.config.TemporalConstants.Workflows.LI_POST_WORKFLOW;
import static com.sardul3.hireboost.temporal.config.TemporalConstants.Workflows.LI_PROFILE_VISIT_WORKFLOW;

@Component
public class PostGenerationScheduler {

    private final WorkflowClient workflowClient;
    private final TemporalConfigProperties workflowConfig;

    public PostGenerationScheduler(WorkflowClient workflowClient, TemporalConfigProperties workflowConfig) {
        this.workflowClient = workflowClient;
        this.workflowConfig = workflowConfig;
    }

//    @Scheduled(fixedRate = 60000)
    public void schedulePostGeneration() {
        String currentVersion = workflowConfig.getCurrentVersions().getWorkflows()
                .get(LI_POST_WORKFLOW);

        String taskQueue = workflowConfig.getWorkflows()
                .get(LI_POST_WORKFLOW)
                .getVersions().get(currentVersion)
                .getTaskQueue();

        String workflowId = WorkflowIdGenerator.generateWorkflowId(LI_POST_WORKFLOW, workflowConfig);

        PostGenerateAndPublishWorkflow workflow = workflowClient.newWorkflowStub(
                PostGenerateAndPublishWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(taskQueue)
                        .build()
        );

        WorkflowClient.start(workflow::generateAndPost);
        System.out.println("🔄 Scheduled post generation started");
    }
}

