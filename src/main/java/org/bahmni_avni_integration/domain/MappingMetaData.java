package org.bahmni_avni_integration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mapping_metadata")
public class MappingMetaData extends BaseEntity {
    @Column(name = "mapping_group_name")
    private String mappingGroupName;

    @Column(name = "mapping_name")
    private String mappingName;

    @Column(name = "bahmni_value")
    private String bahmniValue;

    @Column(name = "avni_value")
    private String avniValue;

    @Column(name = "about")
    private String about;

    public String getMappingGroupName() {
        return mappingGroupName;
    }

    public void setMappingGroupName(String mappingGroupName) {
        this.mappingGroupName = mappingGroupName;
    }

    public String getMappingName() {
        return mappingName;
    }

    public void setMappingName(String mappingName) {
        this.mappingName = mappingName;
    }

    public String getBahmniValue() {
        return bahmniValue;
    }

    public void setBahmniValue(String bahmniValue) {
        this.bahmniValue = bahmniValue;
    }

    public String getAvniValue() {
        return avniValue;
    }

    public void setAvniValue(String avniValue) {
        this.avniValue = avniValue;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}