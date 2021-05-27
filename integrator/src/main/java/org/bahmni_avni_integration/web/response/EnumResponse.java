package org.bahmni_avni_integration.web.response;

import org.bahmni_avni_integration.integration_data.domain.BaseEnum;

public class EnumResponse {
    private int id;
    private String name;

    public EnumResponse(BaseEnum baseEnum) {
        this.id = baseEnum.getValue();
        this.name = baseEnum.name();
    }

    public EnumResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
