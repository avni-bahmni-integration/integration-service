package org.avni_integration_service.integration_data.domain;

public abstract class BaseEnum {
    private final int value;
    private final String name;

    public BaseEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String name() {
        return name;
    }
}
