package com.sardul3.hireboost.autopost.worker;

import com.sardul3.hireboost.autopost.activity.LinkedInPostPublishActivity;
import com.sardul3.hireboost.temporal.config.TemporalConfigProperties;
import com.sardul3.hireboost.temporal.config.TemporalConstants;
import com.sardul3.hireboost.autopost.workflow.LinkedInPostPublishWorkflow;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

    private final TemporalConfigProperties temporalConfigProperties;
    private final LinkedInPostPublishActivity linkedInPostPublishActivity;
    private final WorkerFactory workerFactory;

    public static void main(String[] args) {
        SpringApplication.run(LinkedInPostGenerationWorker.class, args);
    }

    @Override
    public void run(String... args) {
        String taskQueue = temporalConfigProperties.getWorkers()
                .get(TemporalConstants.Workers.LI_POST_WORKER).getTaskQueue();

        Worker worker = workerFactory.newWorker(taskQueue);

        // Register workflow and activity implementations
        worker.registerWorkflowImplementationTypes(LinkedInPostPublishWorkflow.class);
        worker.registerActivitiesImplementations(linkedInPostPublishActivity);

        log.info("Starting LI Post Create Worker...");

        // Start polling for tasks
        workerFactory.start();

        log.info("LI Post Create Worker started successfully.");

    }
}

