package org.aaj.example.messagestorer.service;

import lombok.extern.log4j.Log4j2;
import org.apache.pulsar.reactive.client.api.MessageSendResult;
import org.apache.pulsar.reactive.client.api.MessageSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Log4j2
public class NotifyStoredEventTopicPublisherServiceImpl implements NotifyStoredEventTopicPublisherService {

    private final ReactivePulsarTemplate<String> reactivePulsarTemplate;
    private static final String TOPIC_NAME = "processed-event-topic";

    @Autowired
    public NotifyStoredEventTopicPublisherServiceImpl(ReactivePulsarTemplate<String> reactivePulsarTemplate) {
        this.reactivePulsarTemplate = reactivePulsarTemplate;
    }

    @Override
    public Flux<MessageSendResult<String>> publish(Flux<String> eventToBeSentFlux) {
        return reactivePulsarTemplate
                .send(TOPIC_NAME, eventToBeSentFlux.map(MessageSpec::of))
                .onErrorResume(throwable -> {
                    log.error(throwable);
                    return Flux.just();
                });
    }
}
