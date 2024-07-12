package org.aaj.example.messagepreparer.service;

import org.aaj.example.messagepreparer.dto.UnprocessedTopicEventDTO;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class UnprocessedEventTopicConsumerServiceImpl implements UnprocessedEventTopicConsumerService {

    private final ProcessedTopicEventPreparerService processedTopicEventPreparerService;
    private final ProcessedEventTopicPublisherService processedEventTopicPublisherService;

    @Autowired
    public UnprocessedEventTopicConsumerServiceImpl(ProcessedTopicEventPreparerService processedTopicEventPreparerService, ProcessedEventTopicPublisherService processedEventTopicPublisherService) {
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
    public Flux<MessageResult<Void>> consume(Flux<Message<UnprocessedTopicEventDTO>> eventFlux) {

        // Create Sink to store none used message events; this will serve for the acknowledgement needed by the pulsar broker
        Sinks.Many<Message<UnprocessedTopicEventDTO>> eventSink = Sinks.many().multicast().onBackpressureBuffer();

        // The usage of the "var" keyword will increase build time, however, I do recommend including the "var" keyword when using
        // project reactor "Mono" and "Flux"

        // This will emit unprocessed events to eventSink And will extract the value from the "raw" event that comes from pulsar
        var unpreparedTopicEvents = eventFlux
                .doOnNext(
                        unprocessedTopicEventDTOMessage ->
                                eventSink.tryEmitNext(unprocessedTopicEventDTOMessage).orThrow()
                )
                .map(Message::getValue);

        // This will prepare the events for the next topic
        var preparedTopicEvents = processedTopicEventPreparerService.prepare(unpreparedTopicEvents);

        // This will publish the events into the next topic
        var publishedEvents = processedEventTopicPublisherService.publish(preparedTopicEvents);

        // On subscription, the published events are going to be ignored, and will pass on to the eventSink to publish the
        // acknowledgements for the broker to mark the messages as acknowledged
        return publishedEvents.thenMany(eventSink.asFlux().map(MessageResult::acknowledge));
    }

}
