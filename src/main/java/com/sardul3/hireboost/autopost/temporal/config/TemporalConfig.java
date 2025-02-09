package com.sardul3.hireboost.autopost.temporal.config;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.Worker;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class TemporalConfig {

    private static final String TEMPORAL_SERVICE_ADDRESS = "temporal:7233";
    private static final String TASK_QUEUE = "LinkedInPostQueue";

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newInstance(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(TEMPORAL_SERVICE_ADDRESS)
                        .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs service) {
        return WorkflowClient.newInstance(service);
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        return WorkerFactory.newInstance(workflowClient);
    }

    @Bean
    public Worker worker(WorkerFactory workerFactory) {
        return workerFactory.newWorker(TASK_QUEUE);
    }
}
