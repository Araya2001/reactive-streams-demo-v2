package org.aaj.example.messagepreparer.service;

import lombok.extern.log4j.Log4j2;
import org.aaj.example.messagepreparer.dto.ProcessedTopicEventDTO;
import org.apache.pulsar.reactive.client.api.MessageSendResult;
import org.apache.pulsar.reactive.client.api.MessageSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Log4j2
public class ProcessedEventTopicPublisherServiceImpl implements ProcessedEventTopicPublisherService {

    private final ReactivePulsarTemplate<ProcessedTopicEventDTO> reactivePulsarTemplate;
    private static final String TOPIC_NAME = "processed-event-topic";

    @Autowired
    public ProcessedEventTopicPublisherServiceImpl(ReactivePulsarTemplate<ProcessedTopicEventDTO> reactivePulsarTemplate) {
        this.reactivePulsarTemplate = reactivePulsarTemplate;

    }

    @Override
    public Flux<MessageSendResult<ProcessedTopicEventDTO>> publish(Flux<ProcessedTopicEventDTO> eventToBeSentFlux) {
        return reactivePulsarTemplate
                .send(TOPIC_NAME, eventToBeSentFlux.map(MessageSpec::of))
                .onErrorResume(throwable -> {
                    log.error(throwable);
                    return Flux.just();
                });
    }
}
