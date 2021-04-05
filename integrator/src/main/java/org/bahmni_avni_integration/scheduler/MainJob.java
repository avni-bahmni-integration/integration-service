package org.bahmni_avni_integration.scheduler;

import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.FailedEventRepository;
import org.bahmni_avni_integration.worker.avni.SubjectWorker;
import org.bahmni_avni_integration.worker.bahmni.PatientWorker;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DisallowConcurrentExecution
public class MainJob implements Job {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PatientWorker patientWorker;
    @Autowired
    private SubjectWorker subjectWorker;
    @Autowired
    private ConstantsRepository constantsRepository;
    @Value("${app.tasks}")
    private String tasks;

    @Autowired
    private FailedEventRepository failedEventRepository;

    public void execute(JobExecutionContext context) {
        logger.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
        try {
            failedEventRepository.deleteAll();
            List<IntegrationTask> tasks = IntegrationTask.getTasks(this.tasks);
            Constants allConstants = constantsRepository.findAllConstants();

            if (hasTask(tasks, IntegrationTask.AvniSubject))
                subjectWorker.processSubjects(allConstants);
            if (hasTask(tasks, IntegrationTask.BahmniPatient)) {
                patientWorker.processPatients(allConstants);
            }
        } catch (Exception e) {
            logger.error("Error calling API", e);
        }
        logger.info("Next job scheduled @ {}", context.getNextFireTime());
    }

    private boolean hasTask(List<IntegrationTask> tasks, IntegrationTask task) {
        return tasks.stream().filter(integrationTask -> integrationTask.equals(task)).findAny().orElse(null) != null;
    }
}
