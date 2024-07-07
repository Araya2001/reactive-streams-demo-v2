package org.aaj.example.messagepreparer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarMessage;

import java.time.Instant;

@Getter
@Setter
@Builder
@PulsarMessage(schemaType = SchemaType.JSON)
public class UnprocessedTopicEventDTO {
    private String body;
    private Instant timestamp;
}
