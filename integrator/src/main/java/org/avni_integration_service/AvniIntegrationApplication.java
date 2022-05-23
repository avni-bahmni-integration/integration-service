package org.avni_integration_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AvniIntegrationApplication {
	public static void main(String[] args) {
	    ConfigurableApplicationContext context = SpringApplication.run(AvniIntegrationApplication.class, args);
	    if (args.length > 0 && args[0].equals("CloseOnStart"))
	        context.close();
    }
}
