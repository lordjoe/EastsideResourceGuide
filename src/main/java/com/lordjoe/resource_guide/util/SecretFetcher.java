package com.lordjoe.resource_guide.util;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretVersionName;

public class SecretFetcher {

    public static String getJdbcUrlFromSecret() throws Exception {
        // Project ID and secret name
        String projectId = "lordjoehrd";
        String secretId = "AIVEN_JDBC";
        String versionId = "latest";

        // Build the resource name
        SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);

        // Create the Secret Manager client
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                    .setName(secretVersionName.toString())
                    .build();

            AccessSecretVersionResponse response = client.accessSecretVersion(request);
            return response.getPayload().getData().toStringUtf8();  // The JDBC URL
        }
    }

    public static void main(String[] args) {
        try {
            String jdbcUrl = getJdbcUrlFromSecret();
            System.out.println("Fetched JDBC URL from Secret Manager:");
            System.out.println(jdbcUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

