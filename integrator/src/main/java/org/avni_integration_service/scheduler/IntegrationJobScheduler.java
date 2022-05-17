package org.avni_integration_service.scheduler;

import org.avni_integration_service.bahmni.job.FullErrorJob;
import org.avni_integration_service.bahmni.job.MainJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IntegrationJobScheduler {
    private final MainJob mainJob;
    private final FullErrorJob fullErrorJob;

    @Autowired
    public IntegrationJobScheduler(MainJob mainJob, FullErrorJob fullErrorJob) {
        this.mainJob = mainJob;
        this.fullErrorJob = fullErrorJob;
    }

    @Scheduled(cron = "${app.cron.main}")
    public void mainBahmniJob() {
        mainJob.execute();
    }

    @Scheduled(cron = "${app.cron.full.error}")
    public void fullErrorBahmniJob() {
        fullErrorJob.execute();
    }
}
