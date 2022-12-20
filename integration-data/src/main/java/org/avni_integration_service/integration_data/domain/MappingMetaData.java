package org.avni_integration_service.integration_data.domain;

import org.avni_integration_service.integration_data.domain.framework.BaseIntegrationSpecificEntity;
import org.avni_integration_service.util.ObsDataType;

import javax.persistence.*;

@Entity
@Table(name = "mapping_metadata")
public class MappingMetaData extends BaseIntegrationSpecificEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_group_id")
    private MappingGroup mappingGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_type_id")
    private MappingType mappingType;

    @Column(name = "int_system_value")
    private String intSystemValue;

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

    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }

    public MappingType getMappingType() {
        return mappingType;
    }

    public String getIntSystemValue() {
        return intSystemValue;
    }

    public void setIntSystemValue(String intSystemValue) {
        this.intSystemValue = intSystemValue;
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

    public boolean isText() {
        return ObsDataType.Text.equals(getDataTypeHint());
    }

    public void setDataTypeHint(ObsDataType dataTypeHint) {
        this.dataTypeHint = dataTypeHint;
    }
}
