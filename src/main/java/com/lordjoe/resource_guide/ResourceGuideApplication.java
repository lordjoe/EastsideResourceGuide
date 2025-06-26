package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.DatabaseConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "com.lordjoe", // ensures scanning includes your repository
        exclude = { DataSourceAutoConfiguration.class }
)
public class ResourceGuideApplication {
    public static void main(String[] args) {
        if(args.length==   0
           || !args[0].equalsIgnoreCase("local"))
            DatabaseConnection.setREMOTE();
        if(args.length>1 )
               if(args[1].equalsIgnoreCase("test"))
                   DatabaseConnection.setTEST();
        SpringApplication.run(ResourceGuideApplication.class, args);
    }
}
