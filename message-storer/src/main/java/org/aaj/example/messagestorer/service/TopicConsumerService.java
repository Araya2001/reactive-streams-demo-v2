package org.aaj.example.messagestorer.service;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.reactive.client.api.MessageResult;
import reactor.core.publisher.Flux;

public interface TopicConsumerService<EventPayload> {
    Flux<MessageResult<Void>> consume(Flux<Message<EventPayload>> eventFlux);
}
