package org.aaj.example.publisherwebapp.backend.service;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotifyStoredEventTopicSubscriberServiceImpl implements NotifyStoredEventTopicSubscriberService {

    private final StoredEventTopicEventNotifierService eventNotifierService;

    @Autowired
    public NotifyStoredEventTopicSubscriberServiceImpl(StoredEventTopicEventNotifierService eventNotifierService) {
        this.eventNotifierService = eventNotifierService;
    }

    @Override
    @ReactivePulsarListener(
            schemaType = SchemaType.STRING,
            subscriptionName = "processed-event-topic-subscription",
            stream = true,
            topics = "notify-stored-event-topic"
    )
    public Flux<MessageResult<Void>> subscribe(Flux<Message<String>> eventFlux) {
        // Sink for Acknowledgement of messages
        Sinks.Many<Message<String>> eventSink = Sinks.many().multicast().onBackpressureBuffer();

        // This will extract the messages from the topic event message wrapper
        var extractedEvents = eventFlux
                .doOnNext(
                        eventMessage ->
                                eventSink.tryEmitNext(eventMessage).orThrow()
                )
                .map(Message::getValue);

        // This will publish new events into a local Hot Stream for the Vaadin web to capture push notifications
        var publishedEventsFromNotifyStoredEventTopic = eventNotifierService.publish(extractedEvents);

        // On subscription, the published events are going to be ignored, and will pass on to the eventSink to publish the
        // acknowledgements for the broker to mark the messages as acknowledged
        return publishedEventsFromNotifyStoredEventTopic.thenMany(eventSink.asFlux().map(MessageResult::acknowledge));
    }
}
