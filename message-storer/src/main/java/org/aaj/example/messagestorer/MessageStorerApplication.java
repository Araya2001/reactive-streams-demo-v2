package org.aaj.example.messagestorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.pulsar.annotation.EnablePulsar;

@SpringBootApplication
@EnablePulsar
public class MessageStorerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageStorerApplication.class, args);
    }

}
