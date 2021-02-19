package org.bahmni_avni_integration.scheduler;

import org.bahmni_avni_integration.worker.bahmni.PatientWorker;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleJob implements Job {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PatientWorker patientWorker;

    public void execute(JobExecutionContext context) {
        logger.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
        try {
//        subjectWorker.processSubjects();
            patientWorker.processPatients();
        } catch (Exception e) {
            logger.error("Error calling API", e);
        }
        logger.info("Next job scheduled @ {}", context.getNextFireTime());
    }
}
