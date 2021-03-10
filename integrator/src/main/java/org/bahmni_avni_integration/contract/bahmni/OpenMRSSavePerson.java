package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenMRSSavePerson {
    private List<OpenMRSSaveName> names;
    private String gender;

    public List<OpenMRSSaveName> getNames() {
        return names;
    }

    public void setNames(List<OpenMRSSaveName> names) {
        this.names = names;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}