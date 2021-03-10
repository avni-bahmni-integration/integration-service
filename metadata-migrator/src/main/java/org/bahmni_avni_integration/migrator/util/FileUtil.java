package org.bahmni_avni_integration.migrator.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
    public static String readFile(String fileName) {
        try {
            ClassLoader classLoader = FileUtil.class.getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            return Files.readString(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}