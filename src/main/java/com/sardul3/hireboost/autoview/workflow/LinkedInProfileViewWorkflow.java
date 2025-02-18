package com.sardul3.hireboost.autoview.workflow;

import com.sardul3.hireboost.autoview.model.PersonProfile;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

@WorkflowInterface
public interface LinkedInProfileViewWorkflow {
    @WorkflowMethod
    List<PersonProfile> visitProfiles(String username, String password);

}
