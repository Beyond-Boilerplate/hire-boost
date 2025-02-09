package com.sardul3.hireboost.autopost.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PostGenerateAndPublishWorkflow {
    @WorkflowMethod
    void generateAndPost();
}
