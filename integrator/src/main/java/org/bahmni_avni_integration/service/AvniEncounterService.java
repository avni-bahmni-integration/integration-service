package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSDefaultEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.bahmni.BahmniSplitEncounter;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.mapper.avni.EncounterMapper;
import org.bahmni_avni_integration.mapper.bahmni.OpenMRSEncounterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AvniEncounterService extends BaseAvniEncounterService {
    private final OpenMRSEncounterMapper openMRSEncounterMapper;

    private final AvniEncounterRepository avniEncounterRepository;

    private final OpenMRSEncounterRepository openMRSEncounterRepository;

    private final EncounterMapper encounterMapper;

    private final VisitService visitService;

    private final ErrorService errorService;

    private static final Logger logger = Logger.getLogger(AvniEncounterService.class);

    @Autowired
    public AvniEncounterService(PatientService patientService, MappingMetaDataRepository mappingMetaDataRepository, OpenMRSEncounterRepository openMRSEncounterRepository, OpenMRSEncounterMapper openMRSEncounterMapper, AvniEncounterRepository avniEncounterRepository, PatientService patientService1, MappingMetaDataRepository mappingMetaDataRepository1, OpenMRSEncounterRepository openMRSEncounterRepository1, EncounterMapper encounterMapper, VisitService visitService, ErrorService errorService) {
        super(patientService, mappingMetaDataRepository, openMRSEncounterRepository);
        this.openMRSEncounterMapper = openMRSEncounterMapper;
        this.avniEncounterRepository = avniEncounterRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository1;
        this.openMRSEncounterRepository = openMRSEncounterRepository1;
        this.encounterMapper = encounterMapper;
        this.visitService = visitService;
        this.errorService = errorService;
    }

    public void update(BahmniSplitEncounter bahmniSplitEncounter, GeneralEncounter existingAvniEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(bahmniSplitEncounter, bahmniEncounterToAvniEncounterMetaData, avniPatient);
        avniEncounterRepository.update(existingAvniEncounter.getUuid(), encounter);
    }

    public void updateLabEncounter(OpenMRSFullEncounter fullEncounter, GeneralEncounter existingAvniEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient) {
        GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(fullEncounter, bahmniEncounterToAvniEncounterMetaData, avniPatient);
        avniEncounterRepository.update(existingAvniEncounter.getUuid(), encounter);
    }

    public GeneralEncounter getGeneralEncounter(BahmniSplitEncounter bahmniSplitEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        Map<String, Object> obsCriteria = Map.of(metaData.getBahmniEntityUuidConcept(), bahmniSplitEncounter.getOpenMRSEncounterUuid());
        // OpenMRS encounter uuid will be shared by multiple entities in Avni, hence encounter type is required
        return avniEncounterRepository.getEncounter(metaData.getAvniMappedName(bahmniSplitEncounter.getFormConceptSetUuid()), obsCriteria);
    }

    public GeneralEncounter getLabResultGeneralEncounter(OpenMRSFullEncounter openMRSFullEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        Map<String, Object> obsCriteria = Map.of(metaData.getBahmniEntityUuidConcept(), openMRSFullEncounter.getUuid());
        return avniEncounterRepository.getEncounter(metaData.getLabEncounterTypeMapping().getAvniValue(), obsCriteria);
    }

    public void create(BahmniSplitEncounter splitEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        if (splitEncounter.isVoided()) return;

        GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(splitEncounter, metaData, avniPatient);
        avniEncounterRepository.create(encounter);
    }

    public void createLabEncounter(OpenMRSFullEncounter openMRSFullEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient) {
        if (openMRSFullEncounter.isVoided()) return;

        GeneralEncounter encounter = openMRSEncounterMapper.mapToAvniEncounter(openMRSFullEncounter, metaData, avniPatient);
        avniEncounterRepository.create(encounter);
    }

    public void createDrugOrderEncounter(OpenMRSFullEncounter openMRSFullEncounter, BahmniEncounterToAvniEncounterMetaData metaData, GeneralEncounter avniPatient, OpenMRSDefaultEncounter defaultEncounter) {
        if (openMRSFullEncounter.isVoided()) return;

        GeneralEncounter encounter = openMRSEncounterMapper.mapDrugOrderEncounterToAvniEncounter(openMRSFullEncounter, metaData, avniPatient, defaultEncounter);
        avniEncounterRepository.create(encounter);
    }

    public void updateDrugOrderEncounter(OpenMRSFullEncounter fullEncounter, GeneralEncounter existingAvniEncounter, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, GeneralEncounter avniPatient, OpenMRSDefaultEncounter defaultEncounter) {
        GeneralEncounter encounter = openMRSEncounterMapper.mapDrugOrderEncounterToAvniEncounter(fullEncounter, bahmniEncounterToAvniEncounterMetaData, avniPatient, defaultEncounter);
        avniEncounterRepository.update(existingAvniEncounter.getUuid(), encounter);
    }

    public GeneralEncounter getDrugOrderGeneralEncounter(OpenMRSFullEncounter openMRSEncounter, BahmniEncounterToAvniEncounterMetaData metaData) {
        Map<String, Object> obsCriteria = Map.of(metaData.getBahmniEntityUuidConcept(), openMRSEncounter.getUuid());
        // OpenMRS encounter uuid will be shared by multiple entities in Avni, hence encounter type is required
        return avniEncounterRepository.getEncounter(metaData.getDrugOrderEncounterTypeMapping().getAvniValue(), obsCriteria);
    }

    public boolean shouldFilterEncounter(GeneralEncounter generalEncounter) {
        return !generalEncounter.isCompleted();
    }

    public OpenMRSFullEncounter createCommunityEncounter(GeneralEncounter generalEncounter, OpenMRSPatient patient, Constants constants) {
        if (generalEncounter.getVoided()) {
            logger.debug(String.format("Skipping voided Avni encounter %s", generalEncounter.getUuid()));
            return null;
        }

        var visit = visitService.getAvniRegistrationVisit(patient.getUuid());
        logger.debug(String.format("Creating new Bahmni Encounter for Avni general encounter %s", generalEncounter.getUuid()));
        var openMRSEncounter = encounterMapper.mapEncounter(generalEncounter, patient.getUuid(), constants, visit);
        var savedEncounter = openMRSEncounterRepository.createEncounter(openMRSEncounter);

        errorService.successfullyProcessed(generalEncounter);
        return savedEncounter;
    }

    public void updateCommunityEncounter(OpenMRSFullEncounter existingEncounter, GeneralEncounter generalEncounter, Constants constants) {
        if (generalEncounter.getVoided()) {
            logger.debug(String.format("Voiding Bahmni Encounter %s because the Avni general encounter %s is voided",
                    existingEncounter.getUuid(),
                    generalEncounter.getUuid()));
            openMRSEncounterRepository.voidEncounter(existingEncounter);
        } else {
            logger.debug(String.format("Updating existing Bahmni general encounter %s", existingEncounter.getUuid()));
            var openMRSEncounter = encounterMapper.mapEncounterToExistingEncounter(existingEncounter,
                    generalEncounter,
                    constants);
            openMRSEncounterRepository.updateEncounter(openMRSEncounter);
            errorService.successfullyProcessed(generalEncounter);
        }
    }

    public void processPatientNotFound(GeneralEncounter encounter) {
        errorService.errorOccurred(encounter, ErrorType.NoPatientWithId);
    }
}
