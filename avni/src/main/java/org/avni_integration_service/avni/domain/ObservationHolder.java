package org.avni_integration_service.avni.domain;

import java.util.Map;

public interface ObservationHolder {
    void addObservation(String conceptName, Object value);

    Map<String, Object> getObservations();
}
