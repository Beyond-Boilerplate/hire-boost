package com.sardul3.hireboost.temporal.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * TemporalConfig is a Spring Boot configuration class responsible for setting up Temporal components.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Configuring and creating a {@link WorkflowClient} bean.</li>
 *     <li>Configuring and managing the {@link WorkerFactory}.</li>
 *     <li>Registering workflows and activities.</li>
 * </ul>
 *
 * This setup ensures a seamless integration of Temporal into the Spring Boot environment.
 */
@Configuration
@Slf4j
public class TemporalConfig {

    /**
     * Creates and configures a {@link WorkflowClient} bean that connects to the Temporal service.
     *
     * @return a new instance of {@link WorkflowClient} connected to the Temporal service.
     */
    @Bean
    public WorkflowClient workflowClient(TemporalConfigProperties temporalConfigProperties) {
        WorkflowServiceStubs serviceStubs = createWorkflowServiceStubs(temporalConfigProperties);
        configureNamespace(serviceStubs, temporalConfigProperties);
        return createWorkflowClient(serviceStubs, temporalConfigProperties);
    }

    /**
     * Creates and configures a {@link WorkerFactory} bean.
     *
     * @param workflowClient the WorkflowClient that connects workers to the Temporal service.
     * @return a new instance of {@link WorkerFactory}.
     */
    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        if (workflowClient == null) {
            log.warn("WorkflowClient is null. WorkerFactory will not be created.");
            return null;
        }
        return WorkerFactory.newInstance(workflowClient);
    }

    /**
     * Creates WorkflowServiceStubs with the configured options.
     *
     * @param temporalConfigProperties The configuration properties.
     * @return WorkflowServiceStubs instance.
     */
    private WorkflowServiceStubs createWorkflowServiceStubs(TemporalConfigProperties temporalConfigProperties) {
        WorkflowServiceStubsOptions stubOptions = WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalConfigProperties.getServer())
                .setRpcTimeout(Duration.ofSeconds(30))
                .build();

        log.info("Attempting to connect to Temporal server at {}...", temporalConfigProperties.getServer());
        return WorkflowServiceStubs.newServiceStubs(stubOptions);
    }

    /**
     * Configures the namespace using the provided service stubs.
     *
     * @param service The WorkflowServiceStubs instance.
     * @param temporalConfigProperties The configuration properties.
     */
    private void configureNamespace(WorkflowServiceStubs service, TemporalConfigProperties temporalConfigProperties) {
        TemporalNameSpaceManagement namespaceHelper = new TemporalNameSpaceManagement(service);
        namespaceHelper.namespace(temporalConfigProperties.getNamespace(), 30); // 30-day retention period
    }

    /**
     * Creates a WorkflowClient with the provided service stubs.
     *
     * @param service The WorkflowServiceStubs instance.
     * @param temporalConfigProperties The configuration properties.
     * @return The configured WorkflowClient instance.
     */
    private WorkflowClient createWorkflowClient(WorkflowServiceStubs service, TemporalConfigProperties temporalConfigProperties) {
        WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
                .setNamespace(temporalConfigProperties.getNamespace())
                .build();

        log.info("Successfully connected to Temporal server with namespace: {}", temporalConfigProperties.getNamespace());
        return WorkflowClient.newInstance(service, clientOptions);
    }
}
