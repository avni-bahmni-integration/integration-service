package org.avni_integration_service.bahmni;

import org.avni_integration_service.integration_data.domain.MappingGroup;

public class BahmniMappingGroup {
    public static final MappingGroup PatientSubject = new MappingGroup("PatientSubject");
    public static final MappingGroup GeneralEncounter = new MappingGroup("GeneralEncounter");
    public static final MappingGroup ProgramEnrolment = new MappingGroup("ProgramEnrolment");
    public static final MappingGroup ProgramEncounter = new MappingGroup("ProgramEncounter");
    public static final MappingGroup Observation = new MappingGroup("Observation");
    public static final MappingGroup Common = new MappingGroup("Common");
}
