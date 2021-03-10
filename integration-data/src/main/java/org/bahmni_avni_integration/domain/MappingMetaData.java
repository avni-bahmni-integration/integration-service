package org.bahmni_avni_integration.domain;

import javax.persistence.*;

@Entity
@Table(name = "mapping_metadata")
public class MappingMetaData extends BaseEntity {
    @Column(name = "mapping_group_name")
    @Enumerated(EnumType.STRING)
    private MappingGroup mappingGroup;

    @Column(name = "mapping_name")
    @Enumerated(EnumType.STRING)
    private MappingType mappingType;

    @Column(name = "bahmni_value")
    private String bahmniValue;

    @Column(name = "avni_value")
    private String avniValue;

    @Column(name = "about")
    private String about;

    @Column (name = "data_type_hint")
    @Enumerated(EnumType.STRING)
    private ObsDataType dataTypeHint;

    public MappingGroup getMappingGroup() {
        return mappingGroup;
    }

    public void setMappingGroup(MappingGroup mappingGroup) {
        this.mappingGroup = mappingGroup;
    }

    public MappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
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

    public ObsDataType getDataTypeHint() {
        return dataTypeHint;
    }

    public boolean isCoded() {
        return ObsDataType.Coded.equals(getDataTypeHint());
    }

    public void setDataTypeHint(ObsDataType dataTypeHint) {
        this.dataTypeHint = dataTypeHint;
    }
}