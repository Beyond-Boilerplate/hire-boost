package com.sardul3.hireboost.temporal.config;

public class TemporalConstants {
    public static class Workflows {
        public static final String LI_POST_WORKFLOW = "liPostWorkflow";
        public static final String LI_PROFILE_VISIT_WORKFLOW = "liProfileVisitWorkflow";
        public static final String DEFAULT_VERSION = "default";
        public static final String VERSION_1 = "v1";
    }

    public static class Activities {
        public static final String LI_POST_ACTIVITIES = "liPostActivities";
        public static final String LI_PROFILE_VISIT_ACTIVITIES = "liProfileVisitActivities";
    }

    public static class KeyGenerationStrategies {
        public static final String UUID = "UUID";
    }

    public static class Workers {
        public static final String LI_POST_WORKER =  "liPostWorker";
        public static final String LI_PROFILE_VISIT_WORKER =  "liProfileVisitWorker";
    }
}
