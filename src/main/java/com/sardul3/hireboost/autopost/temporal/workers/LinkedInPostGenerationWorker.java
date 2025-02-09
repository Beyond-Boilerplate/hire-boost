package com.sardul3.hireboost.autopost.temporal.workers;

import com.sardul3.hireboost.autopost.temporal.activities.LinkedInPostPublishActivity;
import com.sardul3.hireboost.autopost.temporal.workflow.LinkedInPostPublishWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@AllArgsConstructor
@Profile({"linkedin-post-worker"})
@Slf4j
public class LinkedInPostGenerationWorker implements CommandLineRunner {

    private final LinkedInPostPublishActivity linkedInPostPublishActivity;

    public static void main(String[] args) {
        SpringApplication.run(LinkedInPostGenerationWorker.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Starting Temporal Worker");

        // Connect to Temporal Service
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // Register Worker for the Task Queue
        Worker worker = factory.newWorker("LinkedInPostQueue");

        // Register Workflow Implementations
        worker.registerWorkflowImplementationTypes(LinkedInPostPublishWorkflow.class);

        // Register Activities
        worker.registerActivitiesImplementations(linkedInPostPublishActivity);

        // Start Worker
        factory.start();
    }
}

