package org.bahmni_avni_integration.migrator.repository;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AvniRepository {
    private final ConnectionFactory connectionFactory;
    private static final Logger logger = Logger.getLogger(AvniRepository.class);

    public AvniRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void createForms(List<OpenMRSForm> forms) throws SQLException {
        Connection connection = connectionFactory.getAvniConnection();
        connection.setAutoCommit(false);
        try {
            String encounterTypeInsert = "insert into encounter_type (name, uuid, version, audit_id, organisation_id) values (?, uuid_generate_v4(), 0, create_audit(), (select id from organisation))";
            String formInsert = "insert into form (name, form_type, uuid, version, audit_id, organisation_id) values (?, ?, uuid_generate_v4(), 0, create_audit(), (select id from organisation))";
            String formElementGroupInsert = "insert into form_element_group (name, form_id, uuid, version, audit_id, organisation_id) values (?, (select id from form where name = ?), uuid_generate_v4(), 0, create_audit(), (select id from organisation))";
            String formElementInsert = "insert into form_element (name, display_order, concept_id, form_element_group_id, uuid, version, audit_id, organisation_id)  values (?, ?, (select id from concept where name = ?), (select id from form_element_group where name = ?), uuid_generate_v4(), 0, create_audit(), (select id from organisation))";
            String encounterFormMappingInsert = "insert into form_mapping (form_id, uuid, version, observations_type_entity_id, subject_type_id, audit_id, organisation_id) values ((select id from form where name = ?), uuid_generate_v4(), 0, (select id from encounter_type where name = ?), (select id from subject_type where name = 'Individual'), create_audit(), (select id from organisation))";
            String programEncounterFormMappingInsert = "insert into form_mapping (form_id, uuid, version, observations_type_entity_id, subject_type_id, audit_id, organisation_id, entity_id) values ((select id from form where name = ?), uuid_generate_v4(), 0, (select id from encounter_type where name = ?), (select id from subject_type where name = 'Individual'), create_audit(), (select id from organisation), (select id from program where name = ?))";

            PreparedStatement encounterTypePS = connection.prepareStatement(encounterTypeInsert);
            PreparedStatement formInsertPS = connection.prepareStatement(formInsert);
            PreparedStatement formElementGroupPS = connection.prepareStatement(formElementGroupInsert);
            PreparedStatement formElementPS = connection.prepareStatement(formElementInsert);
            PreparedStatement encounterFormMappingPS = connection.prepareStatement(encounterFormMappingInsert);
            PreparedStatement programEncounterFormMappingPS = connection.prepareStatement(programEncounterFormMappingInsert);

            for (OpenMRSForm form : forms) {
                encounterTypePS.setString(1, form.getFormName());
                encounterTypePS.executeUpdate();
                logger.info("Created encounter type: " + form.getFormName());

                formInsertPS.setString(1, form.getFormName());
                formInsertPS.setString(2, form.getType());
                formInsertPS.executeUpdate();
                logger.info("Created form: " + form.getFormName());

                formElementGroupPS.setString(1, form.getFormName());
                formElementGroupPS.setString(2, form.getFormName());
                formElementGroupPS.executeUpdate();
                logger.info("Created form element group: " + form.getFormName());

                int i = 1;
                for (OpenMRSConcept openMRSConcept : form.getConcepts()) {
                    formElementPS.setString(1, openMRSConcept.getName());
                    formElementPS.setInt(2, i++);
                    formElementPS.setString(3, openMRSConcept.getAvniConceptName());
                    formElementPS.setString(4, form.getFormName());

                    formElementPS.executeUpdate();
                }
                logger.info("Created form elements for form: " + form.getFormName());

                if (form.getProgram() == null) {
                    encounterFormMappingPS.setString(1, form.getFormName());
                    encounterFormMappingPS.setString(2, form.getFormName());
                    encounterFormMappingPS.executeUpdate();
                    logger.info("Created encounter form mapping for form: " + form.getFormName());
                } else {
                    programEncounterFormMappingPS.setString(1, form.getFormName());
                    programEncounterFormMappingPS.setString(2, form.getFormName());
                    programEncounterFormMappingPS.setString(3, form.getProgram());
                    programEncounterFormMappingPS.executeUpdate();
                    logger.info("Created program encounter form mapping for form: " + form.getFormName());
                }
            }
            encounterTypePS.close();
            formInsertPS.close();
            formElementGroupPS.close();
            formElementPS.close();
            encounterFormMappingPS.close();

            connection.rollback();
        } catch (Exception e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    public List<AvniForm> getForms() throws SQLException {
        try (Connection connection = connectionFactory.getAvniConnection()) {
            return fetchForms(connection);
        }
    }

    private List<AvniForm> fetchForms(Connection connection) throws SQLException {
        String formSelect = "select id, name from form where organisation_id in (select id from organisation) and id=1461 and is_voided = false order by id";
        List<AvniForm> forms = new ArrayList<>();
        try (PreparedStatement formPS = connection.prepareStatement(formSelect)) {
            ResultSet formResult = formPS.executeQuery();
            while (formResult.next()) {
                AvniForm form = new AvniForm();
                form.setId(formResult.getLong("id"));
                form.setName(formResult.getString("name"));
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
}