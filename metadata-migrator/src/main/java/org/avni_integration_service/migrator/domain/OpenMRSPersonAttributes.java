package org.avni_integration_service.migrator.domain;

import org.avni_integration_service.bahmni.Names;

import java.util.ArrayList;

public class OpenMRSPersonAttributes extends ArrayList<OpenMRSPersonAttribute> {
    public OpenMRSForm createForm() {
        OpenMRSForm openMRSForm = new OpenMRSForm();
        openMRSForm.setFormName(Names.AvniPatientRegistrationEncounter);
        openMRSForm.setType("Encounter");
        this.forEach(openMRSForm::addTerm);
        return openMRSForm;
    }
}
