package org.aaj.example.publisherwebapp.backend.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventNotifierService<NotificationPayload> {
    Flux<NotificationPayload> getPublisher();
    Mono<NotificationPayload> publish(Mono<NotificationPayload> payload);
    Flux<NotificationPayload> publish(Flux<NotificationPayload> payload);

}
