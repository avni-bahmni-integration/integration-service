package org.avni_integration_service.avni.domain;

import java.util.HashMap;
import java.util.Map;

public class QuestionGroupObservations implements ObservationHolder {
    private Map<String, Object> observations = new HashMap<>();

    @Override
    public void addObservation(String conceptName, Object value) {
        observations.put(conceptName, value);
    }

    @Override
    public Map<String, Object> getObservations() {
        return observations;
    }
}
