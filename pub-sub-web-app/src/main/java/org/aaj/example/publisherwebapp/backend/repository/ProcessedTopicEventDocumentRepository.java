package org.aaj.example.publisherwebapp.backend.repository;


import org.aaj.example.publisherwebapp.backend.model.ProcessedTopicEventDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedTopicEventDocumentRepository extends ReactiveMongoRepository<ProcessedTopicEventDocument, UUID> {
}
