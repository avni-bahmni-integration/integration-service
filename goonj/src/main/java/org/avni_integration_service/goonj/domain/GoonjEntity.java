package org.avni_integration_service.goonj.domain;

import java.util.List;

public interface GoonjEntity {
    List<String> getObservationFields();

    Object getValue(String responseField);
}
