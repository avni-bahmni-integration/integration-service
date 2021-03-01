package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSFullEncounter {
    private String uuid;
    private String encounterDatetime;
    protected List<OpenMRSEncounterProvider> encounterProviders = new ArrayList<>();
    private OpenMRSUuidHolder patient;
    private OpenMRSUuidHolder encounterType;
    private OpenMRSUuidHolder location;
    private Map<String, Object> map = new HashMap<>();

    public OpenMRSUuidHolder getPatient() {
        return patient;
    }

    public void setPatient(OpenMRSUuidHolder patient) {
        this.patient = patient;
    }

    public OpenMRSUuidHolder getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(OpenMRSUuidHolder encounterType) {
        this.encounterType = encounterType;
    }

    public OpenMRSUuidHolder getLocation() {
        return location;
    }

    public void setLocation(OpenMRSUuidHolder location) {
        this.location = location;
    }

    @JsonAnySetter
    public void setAny(String name, Object obj) {
        map.put(name, obj);
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public List<OpenMRSObservation> getLeafObservations() {
        List<Map<String, Object>> observations = (List<Map<String, Object>>) map.get("obs");
        List<OpenMRSObservation> leafObservations = new ArrayList<>();
        observations.forEach(observation -> {
            addLeafObservation(leafObservations, observation);
        });
        return leafObservations;
    }

    private void addLeafObservation(List<OpenMRSObservation> leafObservations, Map<String, Object> observation) {
        List<Map<String, Object>> groupMembers = (List<Map<String, Object>>) observation.get("groupMembers");
        if (groupMembers != null) {
            groupMembers.forEach(groupMember -> addLeafObservation(leafObservations, groupMember));
        } else {
            Map<String, Object> conceptNode = (Map<String, Object>) observation.get("concept");
            OpenMRSObservation openMRSObservation = new OpenMRSObservation();
            openMRSObservation.setConcept((String) conceptNode.get("uuid"));

            Object value = observation.get("value");
            if (value instanceof LinkedHashMap) {
                value = ((LinkedHashMap)value).get("uuid");
            }
            openMRSObservation.setValue(value);
            leafObservations.add(openMRSObservation);
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getEncounterDatetime() {
        return encounterDatetime;
    }
}