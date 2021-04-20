package org.bahmni_avni_integration.config;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class BugsnagConfig {
    @Value("${bugsnag.api.key}")
    private String apiKey;

    @Bean
    public Bugsnag bugsnag() {
        return new Bugsnag(apiKey);
    }
}