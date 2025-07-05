package com.lordjoe.sandhurst;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.google.cloud.storage.*;

import java.io.FileInputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class FirebaseImageLister {

    public static void main(String[] args) throws Exception {
        // Initialize Firebase
        FileInputStream serviceAccount = new FileInputStream(System.getProperty("user.home") + "/.config/firebase/serviceAccountKey.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("sandhurstneighborhood-9aa15.firebasestorage.app") // replace with your bucket
                .build();

        FirebaseApp.initializeApp(options);

        // List files
        Storage storage = StorageClient.getInstance().bucket().getStorage();
        Bucket bucket = storage.get(options.getStorageBucket());

        System.out.println("Listing image URLs:");
        for (Blob blob : bucket.list().iterateAll()) {
            String name = blob.getName();
            if (name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                // Signed URL valid for 1 hour
                URL url = blob.signUrl(1, TimeUnit.HOURS);
                System.out.println(name + " => " + url);
            }
        }
    }
}
