package org.aaj.example.messagestorer.repository;

import org.aaj.example.messagestorer.model.ProcessedTopicEventDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface ProcessedTopicEventDocumentRepository extends ReactiveMongoRepository<ProcessedTopicEventDocument, UUID> {
}
