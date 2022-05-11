package org.avni_integration_service.integration_data.domain;

import javax.persistence.*;

@Entity
@Table(name = "constants") //constant is reserved in postgres
public class Constant extends BaseEntity {
    @Column
    @Enumerated(EnumType.STRING)
    private ConstantKey key;

    @Column
    private String value;

    public Constant() {
    }

    public Constant(ConstantKey key, String value) {
        this.key = key;
        this.value = value;
    }

    public ConstantKey getKey() {
        return key;
    }

    public void setKey(ConstantKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
