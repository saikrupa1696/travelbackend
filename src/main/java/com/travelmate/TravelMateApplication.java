package com.travelmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TimeZone;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class TravelMateApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        loadDotEnvIfExists();
        SpringApplication.run(TravelMateApplication.class, args);
    }

    /**
     * Loads local .env file if present for development without exposing credentials.
     * System environment variables (e.g. from Render or Docker) take precedence.
     */
    private static void loadDotEnvIfExists() {
        File[] candidateFiles = new File[]{ new File(".env"), new File("../.env") };
        for (File file : candidateFiles) {
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String val = line.substring(eqIdx + 1).trim();
                            if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                                val = val.substring(1, val.length() - 1);
                            }
                            if (System.getProperty(key) == null && System.getenv(key) == null) {
                                System.setProperty(key, val);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }
}
