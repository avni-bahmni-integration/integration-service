package org.avni_integration_service.bahmni;

import org.avni_integration_service.integration_data.domain.IgnoredIntegratingConcept;
import org.avni_integration_service.integration_data.domain.MappingMetaData;

import java.util.List;

public class BahmniEncounterToAvniEncounterMetaData implements BahmniToAvniMetaData {
    private String bahmniEntityUuidConcept;
    private List<MappingMetaData> encounterTypeMappings;
    private MappingMetaData labEncounterTypeMapping;
    private MappingMetaData drugOrderEncounterTypeMapping;
    private MappingMetaData drugOrderConceptMapping;
    private List<MappingMetaData> programMappings;
    private List<IgnoredIntegratingConcept> ignoredIntegratingConcepts;

    public String getAvniMappedName(String openmrsEncounterTypeUuid) {
        MappingMetaData mapping = getMappingMetaData(openmrsEncounterTypeUuid);
        if (mapping != null) return mapping.getAvniValue();
        return null;
    }

    private MappingMetaData getMappingMetaData(String openmrsEncounterTypeUuid) {
        return encounterTypeMappings.stream().filter(mappingMetaData -> mappingMetaData.getIntSystemValue().equals(openmrsEncounterTypeUuid)).findFirst().orElse(null);
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
        return encounterTypeMappings.stream().filter(x -> x.getIntSystemValue().equals(openMRSEncounterUuid)).findFirst().orElse(null);
    }

    public void addLabMapping(MappingMetaData labMapping) {
        this.labEncounterTypeMapping = labMapping;
    }

    public MappingMetaData getLabEncounterTypeMapping() {
        return labEncounterTypeMapping;
    }

    public void addDrugOrderMapping(MappingMetaData drugOrderMapping) {
        this.drugOrderEncounterTypeMapping = drugOrderMapping;
    }

    public MappingMetaData getDrugOrderEncounterTypeMapping() {
        return drugOrderEncounterTypeMapping;
    }

    public void addDrugOrderConceptMapping(MappingMetaData drugOrderConceptMapping) {
        this.drugOrderConceptMapping = drugOrderConceptMapping;
    }

    public MappingMetaData getDrugOrderConceptMapping() {
        return drugOrderConceptMapping;
    }

    public void addProgramMapping(List<MappingMetaData> mappings) {
        this.programMappings = mappings;
    }

    public String getAvniProgramName(String formConceptSetUuid) {
        MappingMetaData programMapping = this.programMappings.stream().filter(mappingMetaData -> mappingMetaData.getIntSystemValue().equals(formConceptSetUuid)).findFirst().orElse(null);
        if (programMapping == null) return null;
        return programMapping.getAvniValue();
    }

    public void setIgnoredConcepts(List<IgnoredIntegratingConcept> ignoredIntegratingConcepts) {
        this.ignoredIntegratingConcepts = ignoredIntegratingConcepts;
    }

    public boolean isIgnoredInBahmni(String conceptUuid) {
        return ignoredIntegratingConcepts.stream().filter(ignoredIntegratingConcept -> ignoredIntegratingConcept.getConceptId().equals(conceptUuid)).findFirst().orElse(null) != null;
    }
}
