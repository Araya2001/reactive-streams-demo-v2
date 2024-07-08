package org.aaj.example.messagestorer.service;

import org.aaj.example.messagestorer.dto.ProcessedTopicEventDTO;
import org.aaj.example.messagestorer.model.ProcessedTopicEventDocument;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProcessedTopicEventToDocumentMapperServiceImpl implements ProcessedTopicEventToDocumentMapperService {
    @Override
    public Flux<ProcessedTopicEventDocument> map(Flux<ProcessedTopicEventDTO> receivedEventFlux) {
        return receivedEventFlux
                .map(processedTopicEventDTO ->
                        ProcessedTopicEventDocument
                                .builder()
                                .id(UUID.randomUUID())
                                .processedTopicEvent(processedTopicEventDTO)
                                .dateCreated(Instant.now())
                                .build()
                );
    }
}
