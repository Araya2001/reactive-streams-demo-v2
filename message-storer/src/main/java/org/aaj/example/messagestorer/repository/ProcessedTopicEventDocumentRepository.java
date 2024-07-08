package org.aaj.example.messagestorer.repository;

import org.aaj.example.messagestorer.model.ProcessedTopicEventDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedTopicEventDocumentRepository extends ReactiveMongoRepository<ProcessedTopicEventDocument, UUID> {
}
