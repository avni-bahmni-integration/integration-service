package org.avni_integration_service.web.response;

import org.avni_integration_service.integration_data.domain.framework.BaseEnum;
import org.avni_integration_service.integration_data.domain.framework.NamedEntity;

public class NamedEntityResponse {
    private int id;
    private String name;

    public NamedEntityResponse(BaseEnum baseEnum) {
        this.id = baseEnum.getValue();
        this.name = baseEnum.name();
    }

    public NamedEntityResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public NamedEntityResponse(NamedEntity namedEntity) {
        this.id = namedEntity.getId();
        this.name = namedEntity.getName();
    }

    public NamedEntityResponse() {
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
