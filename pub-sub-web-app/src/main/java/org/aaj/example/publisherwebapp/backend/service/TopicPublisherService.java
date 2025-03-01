package org.aaj.example.publisherwebapp.backend.service;


import org.apache.pulsar.reactive.client.api.MessageSendResult;
import reactor.core.publisher.Flux;

public interface TopicPublisherService<EventToBeSent> {
    Flux<MessageSendResult<EventToBeSent>> publish(Flux<EventToBeSent> eventToBeSentFlux);
}
