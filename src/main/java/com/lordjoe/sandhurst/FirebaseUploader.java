package com.lordjoe.sandhurst;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

public class FirebaseUploader {

    static {
        try {
            InputStream serviceAccount = FirebaseUploader.class.getResourceAsStream("/firebase-secret.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("sandhurstneighborhood-9aa15.firebasestorage.app")
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    public static String uploadFile(MultipartFile file) throws Exception {
        String fileName = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        StorageClient.getInstance().bucket().create(fileName, file.getBytes(), file.getContentType());
        return "https://storage.googleapis.com/sandhurstneighborhood-9aa15.firebasestorage.app/" + fileName;
    }
}
