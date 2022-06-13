package org.avni_integration_service.avni.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.avni_integration_service.util.MapUtil;

public class GeneralEncounter extends AvniBaseEncounter {
    public static final String ExternalIdFieldName = "External ID";
    public void setExternalId(String externalId) {
        map.put(ExternalIdFieldName, externalId);
    }
    @JsonIgnore
    public String getExternalId() {
        return MapUtil.getString(ExternalIdFieldName, this.map);
    }


}
