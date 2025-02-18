package com.sardul3.hireboost.autoview.worker;

import com.sardul3.hireboost.temporal.config.TemporalConfigProperties;
import com.sardul3.hireboost.temporal.config.TemporalConstants;
import com.sardul3.hireboost.autoview.activity.LinkedInProfileViewActivitiesImpl;
import com.sardul3.hireboost.autoview.workflow.LinkedInProfileViewWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@AllArgsConstructor
@Profile({"linkedin-profile-worker"})
@Slf4j
public class LinkedInProfileVisitWorker implements CommandLineRunner {

    private final LinkedInProfileViewActivitiesImpl activity;
    private final WorkerFactory workerFactory;
    private final TemporalConfigProperties temporalConfigProperties;

    public static void main(String[] args) {
        SpringApplication.run(LinkedInProfileVisitWorker.class, args);
    }

    @Override
    public void run(String... args) {
        String taskQueue = temporalConfigProperties.getWorkers()
                .get(TemporalConstants.Workers.LI_PROFILE_VISIT_WORKER).getTaskQueue();

        Worker worker = workerFactory.newWorker(taskQueue);

        // Register workflow and activity implementations
        worker.registerWorkflowImplementationTypes(LinkedInProfileViewWorkflowImpl.class);
        worker.registerActivitiesImplementations(activity);

        log.info("Starting LI Profile Visit Worker...");

        // Start polling for tasks
        workerFactory.start();

        log.info("LI Profile Visit Worker started successfully.");
    }
}

