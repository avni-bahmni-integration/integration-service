package org.avni_integration_service.scheduler;

import org.apache.log4j.Logger;
import org.avni_integration_service.util.AutoWiringSpringBeanJobFactory;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.*;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
public class SpringQuartzScheduler {
    private static final Logger logger = Logger.getLogger(SpringQuartzScheduler.class);
    @Value("${app.cron.main}")
    private String cronExpression;
    @Value("${app.cron.full.error}")
    private String cronExpressionFullError;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        logger.info("Hello world from Spring...");
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        logger.debug("Configuring Job factory");

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerMainJob(@Qualifier("mainJobTrigger") Trigger trigger, @Qualifier("mainJobDetail") JobDetail job, DataSource quartzDataSource) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        logger.debug("Setting the Scheduler up");
        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFullErrorJob(@Qualifier("fullErrorJobTrigger") Trigger trigger, @Qualifier("fullErrorJobDetail") JobDetail job, DataSource quartzDataSource) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        logger.debug("Setting the Scheduler up");
        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    @Bean(name = "mainJobDetail")
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(MainJob.class);
        jobDetailFactory.setName("mainJobDetail");
        jobDetailFactory.setDescription("mainJobDetail");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean(name = "fullErrorJobDetail")
    public JobDetailFactoryBean jobDetailFullError() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(FullErrorJob.class);
        jobDetailFactory.setName("fullErrorJobDetail");
        jobDetailFactory.setDescription("fullErrorJobDetail");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean(name = "mainJobTrigger")
    public CronTriggerFactoryBean trigger(@Qualifier("mainJobDetail") JobDetail job) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setName("Qrtz_Trigger_MainJob");
        trigger.setCronExpression(cronExpression);
        trigger.setJobDetail(job);
        return trigger;
    }

    @Bean(name = "fullErrorJobTrigger")
    public CronTriggerFactoryBean triggerFullErrorProcessing(@Qualifier("fullErrorJobDetail") JobDetail job) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setName("Qrtz_Trigger_FullError");
        trigger.setCronExpression(cronExpressionFullError);
        trigger.setJobDetail(job);
        return trigger;
    }
}
