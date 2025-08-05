package com.lordjoe.resource_guide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * com.lordjoe.resource_guide.LocalResourceGuidsApplication
 * User: Steve
 * Date: 5/24/25
 */
@SpringBootApplication(
        scanBasePackages = "com.lordjoe", // ensures scanning includes your repository
        exclude = { DataSourceAutoConfiguration.class }
)
public class LocalResourceGuidsApplication {

    public static boolean DEBUG_ADMIN_MODE = true;
    public static void main(String[] args) {
        SpringApplication.run(LocalResourceGuidsApplication.class, args);
    }

}
