package org.aaj.example.messagepreparer.service;

import reactor.core.publisher.Flux;

public interface GenericEventPreparerService<ReceivedEvent, EventToBeSent> {
    Flux<EventToBeSent> prepare(Flux<ReceivedEvent> receivedEvents);
}
