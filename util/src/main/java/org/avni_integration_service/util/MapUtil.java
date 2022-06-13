package org.avni_integration_service.util;

import java.util.Map;

public class MapUtil {
    public static String getString(String key, Map map) {
        Object value = map.get(key);
        if (value == null) return null;
        return (String) value;
    }

    public static Integer getInt(String key, Map map) {
        Object value = map.get(key);
        if (value == null) return null;
        return (Integer) value;
    }
}
