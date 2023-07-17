package org.avni_integration_service.integration_data.domain.config;

import java.util.List;

public class IntegrationSystemConfigCollection {
    private final List<IntegrationSystemConfig> integrationSystemConfigs;

    private static final String MAIN_SCHEDULED_JOB_CRON = "main.scheduled.job.cron";
    private static final String ERROR_SCHEDULED_JOB_CRON = "error.scheduled.job.cron";

    public IntegrationSystemConfigCollection(List<IntegrationSystemConfig> integrationSystemConfigs) {
        this.integrationSystemConfigs = integrationSystemConfigs;
    }

    public String getConfigValue(String key) {
        IntegrationSystemConfig integrationSystemConfig = integrationSystemConfigs.stream().filter(x -> x.getKey().equals(key)).findFirst().orElse(null);
        if (integrationSystemConfig == null) {
            throw new RuntimeException(String.format("No config found for key: %s", key));
        }
        return integrationSystemConfig.getValue();
    }

    public String getMainScheduledJobCron() {
        return this.getConfigValue(MAIN_SCHEDULED_JOB_CRON);
    }

    public String getErrorScheduledJobCron() {
        return this.getConfigValue(ERROR_SCHEDULED_JOB_CRON);
    }
}
