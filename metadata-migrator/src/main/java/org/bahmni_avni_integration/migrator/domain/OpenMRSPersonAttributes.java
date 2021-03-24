package org.bahmni_avni_integration.migrator.domain;

import org.bahmni_avni_integration.integration_data.domain.Names;

import java.util.ArrayList;

public class OpenMRSPersonAttributes extends ArrayList<OpenMRSPersonAttribute> {
    public OpenMRSForm createForm() {
        OpenMRSForm openMRSForm = new OpenMRSForm();
        openMRSForm.setFormName(Names.AvniPatientRegistrationEncounter);
        openMRSForm.setType("Encounter");
        this.stream().forEach(openMRSPersonAttribute -> {
            openMRSForm.addConcept(openMRSPersonAttribute.getUuid(), openMRSPersonAttribute.getName());
        });

        return openMRSForm;
    }
}