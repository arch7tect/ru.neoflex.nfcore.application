package ru.neoflex.nfcore.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ru.neoflex.nfcore")
public class ApplicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationApplication.class, args);
    }

}
