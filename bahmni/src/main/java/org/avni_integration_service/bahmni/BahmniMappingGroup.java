package org.avni_integration_service.bahmni;

import org.avni_integration_service.integration_data.domain.MappingGroup;

public class BahmniMappingGroup {
    public static final MappingGroup PatientSubject = new MappingGroup("PatientSubject", 2);
    public static final MappingGroup GeneralEncounter = new MappingGroup("GeneralEncounter", 3);
    public static final MappingGroup ProgramEnrolment = new MappingGroup("ProgramEnrolment", 4);
    public static final MappingGroup ProgramEncounter = new MappingGroup("ProgramEncounter", 5);
    public static final MappingGroup Observation = new MappingGroup("Observation", 6);
}
