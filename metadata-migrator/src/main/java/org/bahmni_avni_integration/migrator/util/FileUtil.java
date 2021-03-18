package org.bahmni_avni_integration.migrator.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileUtil {
    @Value("${app.config.location}")
    private String configLocation;

    public String readFile(String fileName) {
        try {
            ClassLoader classLoader = FileUtil.class.getClassLoader();
            URL resource = classLoader.getResource(fileName);
            File file = new File(resource.getFile());
            return Files.readString(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readConfigFile(String fileName) {
        try {
            File file = new File(configLocation, fileName);
            if (file.exists()) {
                Path path = Path.of(configLocation, fileName);
                return Files.readString(path);
            }
            return readFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}