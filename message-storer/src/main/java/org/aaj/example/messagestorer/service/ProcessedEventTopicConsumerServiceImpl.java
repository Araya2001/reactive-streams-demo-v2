package org.aaj.example.messagestorer.service;

import org.aaj.example.messagestorer.dto.ProcessedTopicEventDTO;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ProcessedEventTopicConsumerServiceImpl implements ProcessedEventTopicConsumerService {

    private final ProcessedTopicEventDocumentService documentService;
    private final ProcessedTopicEventToDocumentMapperService toDocumentMapper;
    private final NotifyStoredEventTopicPublisherService notifyStoredEventTopicPublisherService;

    @Autowired
    public ProcessedEventTopicConsumerServiceImpl(ProcessedTopicEventDocumentService documentService, ProcessedTopicEventToDocumentMapperService toDocumentMapper, NotifyStoredEventTopicPublisherService notifyStoredEventTopicPublisherService) {
        this.documentService = documentService;
        this.toDocumentMapper = toDocumentMapper;
        this.notifyStoredEventTopicPublisherService = notifyStoredEventTopicPublisherService;
    }

    @Override
    @ReactivePulsarListener(
            schemaType = SchemaType.JSON,
            subscriptionName = "processed-event-topic-subscription",
            stream = true,
            topics = "processed-event-topic"
    )
    public Flux<MessageResult<Void>> consume(Flux<Message<ProcessedTopicEventDTO>> eventFlux) {

        // Create Sink to store none used message events; this will serve for the acknowledgement needed by the pulsar broker
        Sinks.Many<Message<ProcessedTopicEventDTO>> eventSink = Sinks.many().multicast().onBackpressureBuffer();

        // The usage of the "var" keyword will increase build time, however, I do recommend including the "var" keyword when using
        // project reactor "Mono" and "Flux"

        // This will extract the messages from the topic event message wrapper
        var extractedEventsFromMessageFlux = eventFlux
                .doOnNext(
                        processedTopicEventDTOMessage ->
                                eventSink.tryEmitNext(processedTopicEventDTOMessage).orThrow()
                )
                .map(Message::getValue);

        // This will convert the message payload to MongoDB Document Objects
        var mappedDocuments = toDocumentMapper.map(extractedEventsFromMessageFlux);

        // This will insert the documents into mongo
        var storedMongoEvents = documentService.insert(mappedDocuments);

        // This will prepare the messages for the Notify Stored Event Topic
        var mappedEventsForNotifyStoredEventTopic = storedMongoEvents
                .map(
                        processedTopicEventDocument ->
                                new StringBuffer()
                                        .append("New stored event in MongoDB: ")
                                        .append(processedTopicEventDocument.getId())
                                        .toString()
                );

        // This will publish the Notify Stored Event message into the topic
        var publishedEvents = notifyStoredEventTopicPublisherService.publish(mappedEventsForNotifyStoredEventTopic);

        // On subscription, the published events are going to be ignored, and will pass on to the eventSink to publish the
        // acknowledgements for the broker to mark the messages as acknowledged
        return publishedEvents.thenMany(eventSink.asFlux().map(MessageResult::acknowledge));
    }
}
