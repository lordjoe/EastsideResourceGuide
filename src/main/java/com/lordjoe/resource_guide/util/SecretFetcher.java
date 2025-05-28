package com.lordjoe.resource_guide.util;

import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.lordjoe.utilities.Encrypt;
import org.jetbrains.annotations.NotNull;

public class SecretFetcher {

    public static final String PLANB = "ZWET00CgPNSrtAdTIF3AAEzHcjGFZ/gcf692jU24OioDhC5YOu+a/rDAnyIrhYTOxvaLYOX+CqyIgQD/1yMcehv+q46RqPJtklbV/SFF7dPBZd3o/zG0K9vTi0DyB8qVCthG7eXYMFh6R/TVVkdPAaK8tx+LnqEY";

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

    public static String getPasswordfromSecret()   {
        try {
            String jdbcUrl = getJdbcUrlFromSecret();
            return getPasswordString(jdbcUrl);
        } catch (Exception e) {
            return getPasswordString(Encrypt.decryptString(PLANB));

        }
    }

    public static String getPasswordfromSecretB()   {
          return getPasswordString(Encrypt.decryptString(PLANB));
    }


    @NotNull
    private static String getPasswordString(String jdbcUrl) {
        int index = jdbcUrl.indexOf("avnadmin:");
        jdbcUrl = jdbcUrl.substring(  index + "avnadmin:".length());
        index = jdbcUrl.indexOf("@");
        jdbcUrl = jdbcUrl.substring(  0,index);

        return jdbcUrl;
    }


    public static void main(String[] args) {
        try {
            String jdbcUrl = getJdbcUrlFromSecret();
            System.out.println("Fetched JDBC URL from Secret Manager:");
            System.out.println(jdbcUrl);
            String password = getPasswordfromSecret();
            System.out.println("Fetched Password from Secret Manager:" + password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

