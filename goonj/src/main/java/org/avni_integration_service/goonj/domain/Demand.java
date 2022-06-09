package org.avni_integration_service.goonj.domain;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;

import java.util.Map;

public class Demand extends NamedIntegrationSpecificEntity {

    public static Subject from(Map<String, Object> dispatch) {
        Subject subject = new Subject();
        subject.setSubjectType("Demand");
        subject.set(Subject.AddressFieldName, dispatch.get("foo"));

        return subject;
    }
}
