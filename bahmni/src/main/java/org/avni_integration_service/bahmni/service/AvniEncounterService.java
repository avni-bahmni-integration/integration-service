package org.avni_integration_service.bahmni.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.mapper.OpenMRSEncounterMapper;
import org.avni_integration_service.bahmni.mapper.avni.EncounterMapper;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSDefaultEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.bahmni.BahmniEncounterToAvniEncounterMetaData;
import org.avni_integration_service.bahmni.repository.BahmniSplitEncounter;
import org.avni_integration_service.bahmni.repository.OpenMRSEncounterRepository;
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
    public AvniEncounterService(PatientService patientService, OpenMRSEncounterRepository openMRSEncounterRepository, OpenMRSEncounterMapper openMRSEncounterMapper, AvniEncounterRepository avniEncounterRepository, PatientService patientService1, MappingService mappingService, OpenMRSEncounterRepository openMRSEncounterRepository1, EncounterMapper encounterMapper, VisitService visitService, ErrorService errorService) {
        super(patientService, mappingService, openMRSEncounterRepository);
        this.openMRSEncounterMapper = openMRSEncounterMapper;
        this.avniEncounterRepository = avniEncounterRepository;
        this.mappingService = mappingService;
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
        errorService.errorOccurred(encounter, ErrorType.NoIntEntityWithId);
    }
}
