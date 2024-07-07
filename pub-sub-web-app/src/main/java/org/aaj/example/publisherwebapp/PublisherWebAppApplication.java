package org.aaj.example.publisherwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.pulsar.annotation.EnablePulsar;

@SpringBootApplication
@EnablePulsar
public class PublisherWebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublisherWebAppApplication.class, args);
    }

}
