package org.avni_integration_service.scheduler;

import org.avni_integration_service.bahmni.job.AvniBahmniFullErrorJob;
import org.avni_integration_service.bahmni.job.AvniBahmniMainJob;
import org.avni_integration_service.goonj.job.AvniGoonjFullErrorJob;
import org.avni_integration_service.goonj.job.AvniGoonjMainJob;
import org.avni_integration_service.job.AvniPowerFullErrorJob;
import org.avni_integration_service.job.AvniPowerMainJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(value = "avni.int.auto.close", havingValue = "false")
public class IntegrationJobScheduler {
    private final AvniBahmniMainJob avniBahmniMainJob;
    private final AvniBahmniFullErrorJob avniBahmniFullErrorJob;
    private final AvniGoonjMainJob avniGoonjMainJob;
    private final AvniGoonjFullErrorJob avniGoonjFullErrorJob;
    private final AvniPowerMainJob avniPowerMainJob;
    private final AvniPowerFullErrorJob avniPowerFullErrorJob;

    @Autowired
    public IntegrationJobScheduler(AvniBahmniMainJob avniBahmniMainJob, AvniBahmniFullErrorJob avniBahmniFullErrorJob,
                                   AvniGoonjMainJob avniGoonjMainJob, AvniGoonjFullErrorJob avniGoonjFullErrorJob,
                                   AvniPowerMainJob avniPowerMainJob, AvniPowerFullErrorJob avniPowerFullErrorJob) {
        this.avniBahmniMainJob = avniBahmniMainJob;
        this.avniBahmniFullErrorJob = avniBahmniFullErrorJob;
        this.avniGoonjMainJob = avniGoonjMainJob;
        this.avniGoonjFullErrorJob = avniGoonjFullErrorJob;
        this.avniPowerMainJob = avniPowerMainJob;
        this.avniPowerFullErrorJob = avniPowerFullErrorJob;
    }

    @Scheduled(cron = "${goonj.app.cron.main}")
    public void mainGoonjJob() {
        avniGoonjMainJob.execute();
    }

    @Scheduled(cron = "${power.app.cron.main}")
    public void mainPowerJob() {
        avniPowerMainJob.execute();
    }

    @Scheduled(cron = "${power.app.cron.full.error}")
    public void fullErrorPowerJob() {
        avniPowerFullErrorJob.execute();
    }

    @Scheduled(cron = "${goonj.app.cron.full.error}")
    public void fullErrorGoonjJob() {
        avniGoonjFullErrorJob.execute();
    }
}
