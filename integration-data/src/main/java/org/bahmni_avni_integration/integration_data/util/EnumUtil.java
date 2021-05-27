package org.bahmni_avni_integration.integration_data.util;

import org.bahmni_avni_integration.integration_data.domain.BaseEnum;

import java.util.Arrays;

public class EnumUtil {
    public static BaseEnum findByValue(BaseEnum[] enumMembers, int value) {
        return Arrays.stream(enumMembers).filter(baseEnum -> baseEnum.getValue() == value).findFirst().orElse(null);
    }
}
