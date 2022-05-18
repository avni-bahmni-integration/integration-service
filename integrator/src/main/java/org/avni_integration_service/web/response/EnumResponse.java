package org.avni_integration_service.web.response;

import org.avni_integration_service.integration_data.domain.framework.BaseEnum;

public class EnumResponse {
    private int id;
    private String name;

    public EnumResponse(BaseEnum baseEnum) {
        this.id = baseEnum.getValue();
        this.name = baseEnum.name();
    }

    public EnumResponse(int id, String name) {
        this.id = id;
        this.name = name;
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
