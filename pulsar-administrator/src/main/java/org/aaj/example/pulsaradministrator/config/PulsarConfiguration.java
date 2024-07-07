package org.aaj.example.pulsaradministrator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.PulsarTopic;

@Configuration
public class PulsarConfiguration {
    @Bean
    PulsarTopic unprocessedEventTopic() {
        // This will create a topic named "unprocessed-event-topic" on the public namespace
        return PulsarTopic.builder("unprocessed-event-topic").build();
    }

    @Bean
    PulsarTopic processedEventTopic() {
        // This will create a topic named "processed-event-topic" on the public namespace
        return PulsarTopic.builder("processed-event-topic").build();
    }

}
