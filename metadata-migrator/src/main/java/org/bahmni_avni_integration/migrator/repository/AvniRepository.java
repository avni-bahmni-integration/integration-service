package org.bahmni_avni_integration.migrator.repository;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.Names;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;
import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.config.AvniConfig;
import org.bahmni_avni_integration.migrator.domain.*;
import org.bahmni_avni_integration.migrator.repository.avni.AvniAuditRepository;
import org.bahmni_avni_integration.migrator.repository.avni.AvniConceptRepository;
import org.bahmni_avni_integration.migrator.repository.avni.AvniEncounterTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AvniRepository {
    private final ConnectionFactory connectionFactory;
    private static final Logger logger = Logger.getLogger(AvniRepository.class);
    private final AvniConfig avniConfig;

    @Value("${app.config.common.audit}")
    private boolean useCommonAudit;

    @Autowired
    public AvniRepository(ConnectionFactory connectionFactory, AvniConfig avniConfig) {
        this.connectionFactory = connectionFactory;
        this.avniConfig = avniConfig;
    }

    public void cleanup() throws SQLException {
        String deleteFormsMappings = "delete from form_mapping e using audit where e.audit_id = audit.id and audit.created_by_id = ?";
        String deleteFormElements = "delete from form_element e using audit where e.audit_id = audit.id and audit.created_by_id = ?";
        String deleteForms = "delete from form e using audit where e.audit_id = audit.id and audit.created_by_id = ?";
        String deleteFormElementGroups = "delete from form_element_group e using audit where e.audit_id = audit.id and audit.created_by_id = ?";
        String deleteOperationalEncounterTypes = "delete from operational_encounter_type e using audit where e.audit_id = audit.id and audit.created_by_id = ?";
        String deleteEncounterTypes = "delete from encounter_type e using audit where e.audit_id = audit.id and audit.created_by_id = ?";
        String deleteConceptAnswers = "delete from concept_answer e using audit where e.audit_id = audit.id and audit.created_by_id = ?";
        String deleteConcepts = "delete from concept e using audit where e.audit_id = audit.id and audit.created_by_id = ?";
        String deleteAudits = "delete from audit where id in (select id from audit where created_by_id = ? limit 30)";

        try (Connection connection = connectionFactory.getAvniConnection()) {
            delete(deleteFormsMappings, connection, "Form Mapping");
            delete(deleteFormElements, connection, "Form Element");
            delete(deleteFormElementGroups, connection, "Form Element Group");
            delete(deleteForms, connection, "Form");
            delete(deleteOperationalEncounterTypes, connection, "Operational Encounter Type");
            delete(deleteEncounterTypes, connection, "Encounter Type");
            delete(deleteConceptAnswers, connection, "Concept Answer");
            delete(deleteConcepts, connection, "Concept");
            deleteAudits(deleteAudits, connection);
        }
    }

    private void deleteAudits(String auditDeleteSql, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(auditDeleteSql);
        preparedStatement.setInt(1, avniConfig.getImplementationUserId());
        while (true) {
            int count = preparedStatement.executeUpdate();
            if (count <= 0) break;
            logger.info(String.format("Deleted %d rows of audit", count));
        }
        preparedStatement.close();
    }

    private void delete(String sql, Connection connection, String entityType) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, avniConfig.getImplementationUserId());
        int deletedRowCount = preparedStatement.executeUpdate();
        logger.info(String.format("Deleted %d rows of %s", deletedRowCount, entityType));
        preparedStatement.close();
    }

    public void createForms(List<OpenMRSForm> forms) throws SQLException {
        try (Connection connection = connectionFactory.getAvniConnection()) {
            String formInsert = "insert into form (name, form_type, uuid, version, audit_id, organisation_id) values (?, ?, uuid_generate_v4(), 0, create_audit(?), (select id from organisation))";
            String formElementGroupInsert = "insert into form_element_group (name, form_id, uuid, version, audit_id, organisation_id) values (?, (select id from form where name = ?), uuid_generate_v4(), 0, create_audit(?), (select id from organisation))";
            String formElementInsert = "insert into form_element (name, display_order, concept_id, form_element_group_id, uuid, version, audit_id, organisation_id)  values (?, ?, (select id from concept where name = ?), (select id from form_element_group where name = ?), uuid_generate_v4(), 0, create_audit(?), (select id from organisation))";
            String encounterFormMappingInsert = "insert into form_mapping (form_id, uuid, version, observations_type_entity_id, subject_type_id, audit_id, organisation_id) values ((select id from form where name = ?), uuid_generate_v4(), 0, (select id from encounter_type where name = ?), (select id from subject_type where name = 'Individual'), create_audit(?), (select id from organisation))";
            String programEncounterFormMappingInsert = "insert into form_mapping (form_id, uuid, version, observations_type_entity_id, subject_type_id, audit_id, organisation_id, entity_id) values ((select id from form where name = ?), uuid_generate_v4(), 0, (select id from encounter_type where name = ?), (select id from subject_type where name = 'Individual'), create_audit(?), (select id from organisation), (select id from program where name = ?))";

            PreparedStatement formInsertPS = connection.prepareStatement(formInsert);
            PreparedStatement formElementGroupPS = connection.prepareStatement(formElementGroupInsert);
            PreparedStatement formElementPS = connection.prepareStatement(formElementInsert);
            PreparedStatement encounterFormMappingPS = connection.prepareStatement(encounterFormMappingInsert);
            PreparedStatement programEncounterFormMappingPS = connection.prepareStatement(programEncounterFormMappingInsert);

            AvniEncounterTypeRepository avniEncounterTypeRepository = new AvniEncounterTypeRepository(connection);
            for (OpenMRSForm form : forms) {
                avniEncounterTypeRepository.create(form.getFormName(), avniConfig.getImplementationUserId());

                formInsertPS.setString(1, form.getFormName());
                formInsertPS.setString(2, form.getType());
                formInsertPS.setInt(3, avniConfig.getImplementationUserId());
                formInsertPS.executeUpdate();
                logger.info("Created form: " + form.getFormName());

                formElementGroupPS.setString(1, form.getFormName());
                formElementGroupPS.setString(2, form.getFormName());
                formElementGroupPS.setInt(3, avniConfig.getImplementationUserId());
                formElementGroupPS.executeUpdate();
                logger.info("Created form element group: " + form.getFormName());

                createFormElement(formElementPS, form);

                if (form.getProgram() == null) {
                    encounterFormMappingPS.setString(1, form.getFormName());
                    encounterFormMappingPS.setString(2, form.getFormName());
                    encounterFormMappingPS.setInt(3, avniConfig.getImplementationUserId());
                    encounterFormMappingPS.executeUpdate();
                    logger.info("Created encounter form mapping for form: " + form.getFormName());
                } else {
                    programEncounterFormMappingPS.setString(1, form.getFormName());
                    programEncounterFormMappingPS.setString(2, form.getFormName());
                    programEncounterFormMappingPS.setInt(3, avniConfig.getImplementationUserId());
                    programEncounterFormMappingPS.setString(4, form.getProgram());
                    programEncounterFormMappingPS.executeUpdate();
                    logger.info("Created program encounter form mapping for form: " + form.getFormName());
                }
            }
        }
    }

    private void createFormElement(PreparedStatement formElementPS, OpenMRSForm form) throws SQLException {
        int i = 1;
        for (OpenMRSTerminology openMRSTerm : form.getOpenMRSTerminologies()) {
            try {
                formElementPS.setString(1, openMRSTerm.getAvniName());
                formElementPS.setInt(2, i++);
                formElementPS.setString(3, openMRSTerm.getAvniName());
                formElementPS.setString(4, form.getFormName());
                formElementPS.setInt(5, avniConfig.getImplementationUserId());

                formElementPS.executeUpdate();
            } catch (SQLException sqlException) {
                logger.error(String.format("Could not create form element for: %s", openMRSTerm.getAvniName()));
                throw sqlException;
            }
        }
        logger.info(String.format("Created form elements for form: %s with %d form elements", form.getFormName(), form.getOpenMRSTerminologies().size()));
    }

    public List<AvniForm> getForms() throws SQLException {
        try (Connection connection = connectionFactory.getAvniConnection()) {
            return fetchForms(connection);
        }
    }

    private List<AvniForm> fetchForms(Connection connection) throws SQLException {
        String formSelect = """
                select distinct form.id as form_id, form.name as form_name, form.form_type as form_type, 
                st.name as st_name, p.name as p_name, et.name as et_name
                from form
                        join form_mapping fm on form.id = fm.form_id
                        left join subject_type st on fm.subject_type_id = st.id
                        left join program p on fm.entity_id = p.id
                        left join encounter_type et on fm.observations_type_entity_id = et.id
                where form.organisation_id in (select id from organisation)
                 and form.name not ilike '% (Hospital)'
                 and form.name <> ?
                 and form_type IN (?, ?, ?, ?)
                 and form.is_voided = false
                """;
        List<AvniForm> forms = new ArrayList<>();
        try (PreparedStatement formPS = connection.prepareStatement(formSelect)) {
            formPS.setString(1, Names.AvniPatientRegistrationEncounter);
            formPS.setString(2, AvniFormType.IndividualProfile.name());
            formPS.setString(3, AvniFormType.ProgramEnrolment.name());
            formPS.setString(4, AvniFormType.ProgramEncounter.name());
            formPS.setString(5, AvniFormType.Encounter.name());
            ResultSet formResult = formPS.executeQuery();
            while (formResult.next()) {
                AvniForm form = new AvniForm();
                form.setId(formResult.getLong("form_id"));
                form.setName(formResult.getString("form_name"));
                form.setFormType(AvniFormType.valueOf(formResult.getString("form_type")));
                form.setSubjectType(formResult.getString("st_name"));
                form.setProgram(formResult.getString("p_name"));
                form.setEncounterType(formResult.getString("et_name"));
                form.setFormElementGroups(fetchFormElementGroups(connection, form.getId()));
                forms.add(form);
            }
        }
        return forms;
    }

    private List<AvniFormElementGroup> fetchFormElementGroups(Connection connection, long formId) throws SQLException {
        String fegSelect = "select id, name from form_element_group where form_id = ? and is_voided = false order by display_order";
        List<AvniFormElementGroup> formElementGroups = new ArrayList<>();
        try (PreparedStatement fegPS = connection.prepareStatement(fegSelect)) {
            fegPS.setLong(1, formId);
            ResultSet fegResult = fegPS.executeQuery();
            while (fegResult.next()) {
                AvniFormElementGroup formElementGroup = new AvniFormElementGroup();
                formElementGroup.setId(fegResult.getLong("id"));
                formElementGroup.setName(fegResult.getString("name"));
                formElementGroup.setAvniFormElements(fetchFormElements(connection, formElementGroup.getId()));
                formElementGroups.add(formElementGroup);
            }
        }
        return formElementGroups;
    }

    private List<AvniFormElement> fetchFormElements(Connection connection, long fegId) throws SQLException {
        String feSelect = "select id, name, concept_id from form_element where form_element_group_id = ? and is_voided = false order by display_order";
        List<AvniFormElement> formElements = new ArrayList<>();
        try (PreparedStatement fePS = connection.prepareStatement(feSelect)) {
            fePS.setLong(1, fegId);
            ResultSet feResult = fePS.executeQuery();
            while (feResult.next()) {
                AvniFormElement formElement = new AvniFormElement();
                formElement.setId(feResult.getLong("id"));
                formElement.setConcept(fetchConcept(connection, feResult.getLong("concept_id")));
                formElements.add(formElement);
            }
        }
        return formElements;
    }

    private AvniConcept fetchConcept(Connection connection, long conceptId) throws SQLException {
        AvniConcept concept = new AvniConcept();
        String conceptSelect = "select id, name, data_type from concept where id = ? and is_voided = false";

        try (PreparedStatement conceptPS = connection.prepareStatement(conceptSelect)) {
            conceptPS.setLong(1, conceptId);
            ResultSet conceptResult = conceptPS.executeQuery();
            conceptResult.next();
            concept.setId(conceptResult.getLong("id"));
            concept.setName(conceptResult.getString("name"));
            concept.setDataType(conceptResult.getString("data_type"));
        }

        if (Objects.equals(concept.getDataType(), "Coded")) {
            concept.setAnswerConcepts(fetchAnswerConcepts(connection, conceptId));
        }

        return concept;
    }

    private List<AvniConcept> fetchAnswerConcepts(Connection connection, long conceptId) throws SQLException {
        List<AvniConcept> answerConcepts = new ArrayList<>();
        String answersSelect = "select answer.id, answer.name from concept_answer ca join concept answer on answer.id=ca.answer_concept_id where ca.concept_id = ? and ca.is_voided = false and answer.is_voided=false";
        try (PreparedStatement answersPS = connection.prepareStatement(answersSelect)) {
            answersPS.setLong(1, conceptId);
            ResultSet answersResult = answersPS.executeQuery();
            while (answersResult.next()) {
                AvniConcept answerConcept = new AvniConcept();
                answerConcept.setId(answersResult.getLong("id"));
                answerConcept.setName(answersResult.getString("name"));
                answerConcepts.add(answerConcept);
            }
        }
        return answerConcepts;
    }

    public void savePersonAttributes(List<OpenMRSPersonAttribute> personAttributes) throws SQLException {
        try (Connection connection = connectionFactory.getAvniConnection()) {
            AvniConceptRepository avniConceptRepository = new AvniConceptRepository(connection);
            for (OpenMRSPersonAttribute personAttribute : personAttributes) {
                avniConceptRepository.addConcept(personAttribute.getAvniDataType(), personAttribute.getAvniName(), avniConfig.getImplementationUserId());

                if (personAttribute.getAttributeType() == OpenMRSPersonAttribute.AttributeType.Coded) {
                    int i = 1;
                    for (OpenMRSConcept answerConcept : personAttribute.getAnswers()) {
                        avniConceptRepository.addConcept(answerConcept.getAvniDataType(), answerConcept.getAvniName(), avniConfig.getImplementationUserId());
                        avniConceptRepository.addConceptAnswer(personAttribute.getAvniName(), answerConcept.getAvniName(), i++, avniConfig.getImplementationUserId());
                    }
                }
            }
        }
        logger.info("Saved person attributes as concepts to Avni");
    }

    public void saveConcepts(List<OpenMRSConcept> concepts) throws SQLException {
        try (Connection connection = connectionFactory.getAvniConnection()) {
            int commonAuditId = 0;
            if (useCommonAudit) {
                AvniAuditRepository avniAuditRepository = new AvniAuditRepository(connection);
                commonAuditId = avniAuditRepository.createAudit(avniConfig.getImplementationUserId());
            }

            AvniConceptRepository avniConceptRepository = new AvniConceptRepository(connection);
            int count = 0;
            for (OpenMRSConcept concept : concepts) {
                count++;
                if (useCommonAudit)
                    avniConceptRepository.addConceptToBatchWithCommonAudit(concept.getAvniDataType(), concept.getAvniName(), commonAuditId);
                else
                    avniConceptRepository.addConceptToBatch(concept.getAvniDataType(), concept.getAvniName(), avniConfig.getImplementationUserId());

                if (concept.getAvniDataType().equals(ObsDataType.Coded.name())) {
                    int i = 1;
                    for (OpenMRSConcept answerConcept : concept.getAnswers()) {
                        if (useCommonAudit) {
                            avniConceptRepository.addConceptToBatchWithCommonAudit(answerConcept.getAvniDataType(), answerConcept.getAvniName(), commonAuditId);
                            avniConceptRepository.addConceptAnswerToBatchWithCommonAudit(concept.getAvniName(), answerConcept.getAvniName(), i++, commonAuditId);
                        } else {
                            avniConceptRepository.addConceptToBatch(answerConcept.getAvniDataType(), answerConcept.getAvniName(), avniConfig.getImplementationUserId());
                            avniConceptRepository.addConceptAnswerToBatch(concept.getAvniName(), answerConcept.getAvniName(), i++, avniConfig.getImplementationUserId());
                        }
                    }
                }
                if (count % 20 == 0) {
                    avniConceptRepository.executeConceptBatch();
                    avniConceptRepository.executeConceptAnswerBatch();
                    logger.info("Created 20 more concepts in Avni");
                }
            }
            if (count % 20 > 0) {
                avniConceptRepository.executeConceptBatch();
                avniConceptRepository.executeConceptAnswerBatch();
            }
        }
        logger.info("Created concepts in Avni");
    }

    public void createConcept(ObsDataType dataType, String name) throws SQLException {
        try (Connection connection = connectionFactory.getAvniConnection()) {
            AvniConceptRepository avniConceptRepository = new AvniConceptRepository(connection);
            avniConceptRepository.addConcept(dataType.toString(), name, avniConfig.getImplementationUserId());
        }
    }

    public void createEncounterType(String avniValue, int auditUserId) throws SQLException {
        try (Connection connection = connectionFactory.getAvniConnection()) {
            AvniEncounterTypeRepository avniEncounterTypeRepository = new AvniEncounterTypeRepository(connection);
            avniEncounterTypeRepository.create(avniValue, auditUserId);
        }
    }
}