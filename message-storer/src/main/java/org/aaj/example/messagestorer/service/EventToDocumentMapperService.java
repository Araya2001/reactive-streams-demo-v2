package org.aaj.example.messagestorer.service;

import reactor.core.publisher.Flux;

public interface EventToDocumentMapperService<ReceivedEvent, Document> {
    Flux<Document> map(Flux<ReceivedEvent> receivedEventFlux);
}
