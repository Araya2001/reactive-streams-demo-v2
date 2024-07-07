package org.aaj.example.messagestorer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericDocumentService<Document, Identifier> {
    Mono<Void> delete(Flux<Document> documentFlux);

    Mono<Document> findById(Identifier id);

    Flux<Document> findAll();

    Flux<Document> insert(Flux<Document> documentFlux);
}
