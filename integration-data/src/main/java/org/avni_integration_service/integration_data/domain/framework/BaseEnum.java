package org.avni_integration_service.integration_data.domain.framework;

public interface BaseEnum {
    @Deprecated
    int getValue();
    String getName();
    String name();
}
