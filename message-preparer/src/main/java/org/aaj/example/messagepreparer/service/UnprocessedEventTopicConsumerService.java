package org.aaj.example.messagepreparer.service;

import lombok.extern.log4j.Log4j2;
import org.aaj.example.messagepreparer.dto.ProcessedTopicEventDTO;
import org.aaj.example.messagepreparer.dto.UnprocessedTopicEventDTO;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.apache.pulsar.reactive.client.api.MessageSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Log4j2
public class UnprocessedEventTopicConsumerService implements TopicConsumerService<UnprocessedTopicEventDTO> {

    private final ProcessedTopicEventPreparerService processedTopicEventPreparerService;
    private final ProcessedEventTopicPublisherService processedEventTopicPublisherService;

    @Autowired
    public UnprocessedEventTopicConsumerService(ProcessedTopicEventPreparerService processedTopicEventPreparerService, ProcessedEventTopicPublisherService processedEventTopicPublisherService) {
        this.processedTopicEventPreparerService = processedTopicEventPreparerService;
        this.processedEventTopicPublisherService = processedEventTopicPublisherService;
    }

    @Override
    @ReactivePulsarListener(
            schemaType = SchemaType.JSON,
            subscriptionName = "unprocessed-event-topic-subscription",
            stream = true,
            topics = "unprocessed-event-topic"

    )
    public Flux<MessageResult<Void>> consume(Flux<Message<UnprocessedTopicEventDTO>> batchOfEvents) {

        // Create Sink to store none used message events; this will serve for the acknowledgement needed by the pulsar broker
        Sinks.Many<Message<UnprocessedTopicEventDTO>> batchOfEventsSink = Sinks.many().multicast().onBackpressureBuffer();

        // This will emit unprocessed events to batchOfEventsSink And will extract the value from the "raw" event that comes from pulsar
        Flux<UnprocessedTopicEventDTO> unpreparedTopicEvents = batchOfEvents
                .doOnNext(
                        unprocessedTopicEventDTOMessage ->
                                batchOfEventsSink.tryEmitNext(unprocessedTopicEventDTOMessage).orThrow()
                )
                .map(Message::getValue);

        // This will prepare the events for the next topic
        Flux<ProcessedTopicEventDTO> preparedTopicEvents = processedTopicEventPreparerService.prepare(unpreparedTopicEvents);

        // This will publish the events into the next topic
        Flux<MessageSendResult<ProcessedTopicEventDTO>> publishedEvents = processedEventTopicPublisherService.publish(preparedTopicEvents);

        // On subscription, the published events are going to be ignored, and will pass on to the batchOfEventsSink to publish the
        // acknowledgements for the broker to mark the messages as acknowledged
        return publishedEvents.thenMany(batchOfEventsSink.asFlux().map(MessageResult::acknowledge));
    }

}
