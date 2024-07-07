package org.aaj.example.messagepreparer.service;

import org.aaj.example.messagepreparer.dto.ProcessedTopicEventDTO;
import org.aaj.example.messagepreparer.dto.UnprocessedTopicEventDTO;

public interface ProcessedTopicEventPreparerService extends GenericEventPreparerService<UnprocessedTopicEventDTO, ProcessedTopicEventDTO>{
}
