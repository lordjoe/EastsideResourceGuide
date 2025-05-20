package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.DatabaseConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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
