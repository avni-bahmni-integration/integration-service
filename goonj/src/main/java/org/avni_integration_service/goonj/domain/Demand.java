package org.avni_integration_service.goonj.domain;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;

import java.util.Map;

/*
[
    {
        "TypeOfDisaster": "Not Applicable",
        "TargetCommunity": "None",
        "State": "Maharashtra",
        "NumberOfPeople": null,
        "District": "Mumbai City",
        "DemandName": "2022/ACC-145990/MHR/027",
        "DemandId": "a1CC20000007q6jMAA",
        "AccountName": "Mumbai"
    }
]
 */
public class Demand {
    //TODO make these a DTO
    public static Subject from(Map<String, Object> dispatch) {
        Subject subject = new Subject();
        subject.setSubjectType("Demand");
        subject.set(Subject.AddressFieldName, dispatch.get("foo"));

        return subject;
    }
}
