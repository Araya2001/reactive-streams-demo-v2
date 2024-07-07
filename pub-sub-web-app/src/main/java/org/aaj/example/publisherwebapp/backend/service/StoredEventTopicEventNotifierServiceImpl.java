package org.aaj.example.publisherwebapp.backend.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
@Log4j2
public class StoredEventTopicEventNotifierServiceImpl implements StoredEventTopicEventNotifierService {

    private final Sinks.Many<String> eventSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Flux<String> getPublisher() {
        return eventSink.asFlux();
    }

    @Override
    public Mono<String> publish(Mono<String> payload) {
        return payload.doOnNext(s -> eventSink.tryEmitNext(s).orThrow());
    }

    @Override
    public Flux<String> publish(Flux<String> payload) {
        return payload.doOnNext(s -> eventSink.tryEmitNext(s).orThrow());

    }
}
