package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SubjectMapper {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public OpenMRSEncounter mapSubject(Subject subject) {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        return openMRSEncounter;
//        openMRSEncounter.setEncounterDateTime(new Date());
//        openMRSEncounter.setPatientUuid();
    }
}