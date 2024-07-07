package org.aaj.example.messagepreparer.service;

import org.aaj.example.messagepreparer.dto.ProcessedTopicEventDTO;
import org.aaj.example.messagepreparer.dto.UnprocessedTopicEventDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
public class ProcessedTopicEventPreparerServiceImpl implements ProcessedTopicEventPreparerService {

    @Override
    public Flux<ProcessedTopicEventDTO> prepare(Flux<UnprocessedTopicEventDTO> receivedEvents) {

        return receivedEvents.map(
                unprocessedTopicEventDTO ->
                        ProcessedTopicEventDTO
                                .builder()
                                .body(unprocessedTopicEventDTO.getBody())
                                .timestamp(unprocessedTopicEventDTO.getTimestamp())
                                .preparationMessage("This event has been processed by message-preparer service!")
                                .processUUID(UUID.randomUUID())
                                .build()
        );
    }
}
