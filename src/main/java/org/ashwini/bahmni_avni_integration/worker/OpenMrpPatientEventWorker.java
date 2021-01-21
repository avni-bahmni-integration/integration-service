package org.ashwini.bahmni_avni_integration.worker;

import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenMrpPatientEventWorker implements EventWorker {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(Event event) {
        String content = event.getContent();
        logger.info(event.toString());
        logger.info(content);
    }

    @Override
    public void cleanUp(Event event) {
    }

}
