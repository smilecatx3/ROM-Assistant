package com.github.romassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication
@PropertySource(value="message.properties", encoding="UTF-8")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
