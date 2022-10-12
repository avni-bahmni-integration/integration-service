package org.avni_integration_service.util;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class BugsnagConfig {
    @Autowired
    Environment environment;

    @Bean
    public Bugsnag bugsnag() {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        String apiKey = environment.getProperty("bugsnag.api.key");
        String bugsnagReleaseStage = environment.getProperty("bugsnag.release.stage");
        logger.info(String.format("bugsnagReleaseStage: %s", bugsnagReleaseStage));
        Bugsnag bugsnag = new Bugsnag(apiKey, true);
        bugsnag.setReleaseStage(bugsnagReleaseStage);
        bugsnag.setNotifyReleaseStages("prod", "staging", "prerelease", "uat");
        return bugsnag;
    }
}
