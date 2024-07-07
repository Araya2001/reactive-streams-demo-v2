package org.aaj.example.publisherwebapp.backend.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarMessage;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@PulsarMessage(schemaType = SchemaType.JSON)
public class ProcessedTopicEventDTO {
    private String body;
    private Instant timestamp;
    private String preparationMessage;
    private UUID processUUID;
}
