package org.ashwini.bahmni_avni_integration.scheduler;

import org.ashwini.bahmni_avni_integration.http.AvniHttpClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SampleJob implements Job {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AvniHttpClient avniHttpClient;


    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

        try {
            ResponseEntity<String> response = avniHttpClient.get("/web/concepts");
            logger.info(response.getBody());
        } catch (Exception e) {
            logger.error("Error calling API", e);
        }


        logger.info("Next job scheduled @ {}", context.getNextFireTime());
    }
}
