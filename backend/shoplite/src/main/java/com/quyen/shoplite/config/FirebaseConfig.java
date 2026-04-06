package com.quyen.shoplite.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${shoplite.firebase.credentials-file:}")
    private String credentialsFile;

    @Value("${shoplite.firebase.credentials-json:}")
    private String credentialsJson;

    @Value("${shoplite.firebase.enabled:true}")
    private boolean firebaseEnabled;

    @PostConstruct
    public void initialize() {
        if (!firebaseEnabled) {
            log.warn("[Firebase] Firebase is DISABLED in config. Push notifications will be mocked.");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try {
                InputStream serviceAccount = resolveCredentials();
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("[Firebase] FirebaseApp initialized successfully.");

            } catch (IOException e) {
                log.error("[Firebase] Failed to initialize Firebase. Push notifications will not work. Error: {}", e.getMessage());
                // Không throw exception để app vẫn start được (graceful degradation)
            }
        } else {
            log.info("[Firebase] FirebaseApp already initialized. Skipping.");
        }
    }

    private InputStream resolveCredentials() throws IOException {
        // Ưu tiên 1: đọc từ file path
        if (credentialsFile != null && !credentialsFile.isBlank()) {
            log.info("[Firebase] Loading credentials from file: {}", credentialsFile);
            return new FileInputStream(credentialsFile);
        }

        // Ưu tiên 2: đọc từ JSON string trong properties (dành cho testing)
        if (credentialsJson != null && !credentialsJson.isBlank()) {
            log.info("[Firebase] Loading credentials from inline JSON.");
            return new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
        }

        // Ưu tiên 3: classpath (firebase-service-account.json trong resources)
        InputStream classpathStream = getClass().getClassLoader()
                .getResourceAsStream("firebase-service-account.json");

        if (classpathStream != null) {
            log.info("[Firebase] Loading credentials from classpath: firebase-service-account.json");
            return classpathStream;
        }

        throw new IOException("No Firebase credentials found. Set shoplite.firebase.credentials-file or add firebase-service-account.json to resources.");
    }
}
