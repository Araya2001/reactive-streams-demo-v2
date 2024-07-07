package org.aaj.example.publisherwebapp.backend.service;

import lombok.extern.log4j.Log4j2;
import org.aaj.example.publisherwebapp.backend.dto.UnprocessedTopicEventDTO;
import org.apache.pulsar.reactive.client.api.MessageSendResult;
import org.apache.pulsar.reactive.client.api.MessageSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Log4j2
public class UnprocessedEventTopicPublisherServiceImpl implements UnprocessedEventTopicPublisherService {

    private final ReactivePulsarTemplate<UnprocessedTopicEventDTO> reactivePulsarTemplate;
    private static final String TOPIC_NAME = "unprocessed-event-topic";

    @Autowired
    public UnprocessedEventTopicPublisherServiceImpl(ReactivePulsarTemplate<UnprocessedTopicEventDTO> reactivePulsarTemplate) {
        this.reactivePulsarTemplate = reactivePulsarTemplate;

    }

    @Override
    public Flux<MessageSendResult<UnprocessedTopicEventDTO>> publish(Flux<UnprocessedTopicEventDTO> eventToBeSentFlux) {
        return reactivePulsarTemplate
                .send(TOPIC_NAME, eventToBeSentFlux.map(MessageSpec::of))
                .onErrorResume(throwable -> {
                    log.error(throwable);
                    return Flux.just();
                });
    }
}
