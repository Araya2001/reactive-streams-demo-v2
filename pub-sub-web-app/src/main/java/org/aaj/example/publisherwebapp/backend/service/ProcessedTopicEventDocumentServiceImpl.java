package org.aaj.example.publisherwebapp.backend.service;

import lombok.extern.log4j.Log4j2;
import org.aaj.example.publisherwebapp.backend.model.ProcessedTopicEventDocument;
import org.aaj.example.publisherwebapp.backend.repository.ProcessedTopicEventDocumentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Log4j2
public class ProcessedTopicEventDocumentServiceImpl implements ProcessedTopicEventDocumentService {

    private final ProcessedTopicEventDocumentRepository repository;

    public ProcessedTopicEventDocumentServiceImpl(ProcessedTopicEventDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> delete(Flux<ProcessedTopicEventDocument> documentFlux) {
        return repository
                .deleteAll(documentFlux)
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    @Override
    public Mono<ProcessedTopicEventDocument> findById(UUID id) {
        return repository
                .findById(id)
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    @Override
    public Flux<ProcessedTopicEventDocument> findAll() {
        return repository
                .findAll()
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    @Override
    public Flux<ProcessedTopicEventDocument> insert(Flux<ProcessedTopicEventDocument> documentFlux) {
        return repository
                .insert(documentFlux)
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
