package org.aaj.example.messagepreparer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.pulsar.annotation.EnablePulsar;

@SpringBootApplication
@EnablePulsar
public class MessagePreparerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagePreparerApplication.class, args);
    }

}
