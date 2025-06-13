package com.lordjoe.sandhurst;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication(
        scanBasePackages = "com.lordjoe", // ensures scanning includes your repository
        exclude = { DataSourceAutoConfiguration.class }
)
public class SandhurstApplication {
    @Bean
    CommandLineRunner runner(ApplicationContext ctx) {
        return args -> System.out.println(Arrays.toString(ctx.getBeanDefinitionNames()));
    }
    public static void main(String[] args) {
        SpringApplication.run(SandhurstApplication.class, args);
    }
}

