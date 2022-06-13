package org.avni_integration_service.scheduler;

import org.avni_integration_service.bahmni.job.AvniBahmniFullErrorJob;
import org.avni_integration_service.bahmni.job.AvniBahmniMainJob;
import org.avni_integration_service.goonj.job.AvniGoonjFullErrorJob;
import org.avni_integration_service.goonj.job.AvniGoonjMainJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class IntegrationJobScheduler {
    private final AvniBahmniMainJob avniBahmniMainJob;
    private final AvniBahmniFullErrorJob avniBahmniFullErrorJob;
    private final AvniGoonjMainJob avniGoonjMainJob;
    private final AvniGoonjFullErrorJob avniGoonjFullErrorJob;

    @Autowired
    public IntegrationJobScheduler(AvniBahmniMainJob avniBahmniMainJob, AvniBahmniFullErrorJob avniBahmniFullErrorJob,
                                   AvniGoonjMainJob avniGoonjMainJob, AvniGoonjFullErrorJob avniGoonjFullErrorJob) {
        this.avniBahmniMainJob = avniBahmniMainJob;
        this.avniBahmniFullErrorJob = avniBahmniFullErrorJob;
        this.avniGoonjMainJob = avniGoonjMainJob;
        this.avniGoonjFullErrorJob = avniGoonjFullErrorJob;
    }

    @Scheduled(cron = "${goonj.app.cron.main}")
    public void mainGoonjJob() {
        avniGoonjMainJob.execute();
    }

//    @Scheduled(cron = "${goonj.app.cron.full.error}")
    public void fullErrorGoonjJob() {
        avniGoonjFullErrorJob.execute();
    }
}
