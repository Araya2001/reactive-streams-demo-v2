package org.aaj.example.messagestorer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.aaj.example.messagestorer.dto.ProcessedTopicEventDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document
@Getter
@Setter
@Builder
public class ProcessedTopicEventDocument {

    @Id
    private UUID id;
    private Instant dateCreated;
    private ProcessedTopicEventDTO processedTopicEvent;

}
