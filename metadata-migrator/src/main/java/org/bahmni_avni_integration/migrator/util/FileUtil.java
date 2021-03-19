package org.bahmni_avni_integration.migrator.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    this.getClass().getResourceAsStream(fileName)));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(System.getProperty("line.separator"));
            }
            return stringBuilder.toString();
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