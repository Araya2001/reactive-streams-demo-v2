package org.aaj.example.messagestorer.service;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.reactive.client.api.MessageResult;
import reactor.core.publisher.Flux;

public interface TopicSubscriberService<EventPayload> {
    Flux<MessageResult<Void>> subscribe(Flux<Message<EventPayload>> eventFlux);
}
