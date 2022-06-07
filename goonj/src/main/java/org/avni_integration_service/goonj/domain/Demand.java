package org.avni_integration_service.goonj.domain;

import org.avni_integration_service.avni.domain.Subject;

import java.util.Map;

public class Demand {
    public static Subject from(Map<String, Object> demand) {
        Subject subject = new Subject();
        subject.setSubjectType("Demand");
        subject.set(Subject.AddressFieldName, demand.get("State"));
        return subject;
    }
}
