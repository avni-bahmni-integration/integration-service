package org.bahmni_avni_integration.util;

import java.io.File;

public class TestUtils {
    public static <T> T readResource(String fileName, Class<T> klass) {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        String absolutePath = file.getAbsolutePath();
        T resource = ObjectJsonMapper.readValue(new File(absolutePath), klass);
        return resource;
    }
}