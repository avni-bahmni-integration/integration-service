package org.bahmni_avni_integration.integration_data.internal;

import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;

import java.util.List;

public class BahmniEncounterToAvniEncounterMetaData implements BahmniToAvniMetaData {
    private String bahmniEntityUuidConcept;
    private List<MappingMetaData> encounterTypeMappings;
    private MappingMetaData labMapping;

    public String getAvniMappedName(String openmrsEncounterTypeUuid) {
        MappingMetaData mapping = getMappingMetaData(openmrsEncounterTypeUuid);
        if (mapping != null) return mapping.getAvniValue();
        return null;
    }

    private MappingMetaData getMappingMetaData(String openmrsEncounterTypeUuid) {
        return encounterTypeMappings.stream().filter(mappingMetaData -> mappingMetaData.getBahmniValue().equals(openmrsEncounterTypeUuid)).findFirst().orElse(null);
    }

    public boolean hasBahmniConceptSet(String openmrsEncounterTypeUuid) {
        return getMappingMetaData(openmrsEncounterTypeUuid) != null;
    }

    public void setBahmniEntityUuidConcept(String bahmniEntityUuidConcept) {
        this.bahmniEntityUuidConcept = bahmniEntityUuidConcept;
    }

    public String getBahmniEntityUuidConcept() {
        return bahmniEntityUuidConcept;
    }

    public void addEncounterMappings(List<MappingMetaData> encounterTypeMappings) {
        this.encounterTypeMappings = encounterTypeMappings;
    }

    public MappingMetaData getEncounterMappingFor(String openMRSEncounterUuid) {
        return encounterTypeMappings.stream().filter(x -> x.getBahmniValue().equals(openMRSEncounterUuid)).findFirst().orElse(null);
    }

    public void addLabMapping(MappingMetaData labMapping) {
        this.labMapping = labMapping;
    }

    public MappingMetaData getLabMapping() {
        return labMapping;
    }
}