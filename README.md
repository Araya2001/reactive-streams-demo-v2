# Reactive Streams Demo V2

    Author: Alejandro Araya Jiménez
    Create Date: 2024-07-06
    Update Date: 2024-07-17
    License: Apache-2.0

---

## Introduction

The `reactive-streams-demo-v2` project is a software solution that demonstrates the use of reactive streams in an event-driven architecture.
From what I can tell from the project commit history, the project uses several technologies including Apache Pulsar, MongoDB, Docker,
Temurin Java, Reactive Spring, and Vaadin.

Here are some specifics of the solution:

* **Message Creation and Sending**: The application provides a means to create messages through a UI and send those messages in a
  non-blocking manner.
  The application uses Apache Pulsar to facilitate this message passing.
* **Reactive Services**: The application uses Reactive Spring, a project in the Spring ecosystem that is used to build reactive
  applications.
  This lets your app handle a large number of concurrent connections with a small number of threads.
* **Microservices Structure**: The project includes several microservices that are responsible for different aspects of the application,
  improving modularity and scalability.
* **Persistence**: The solution uses MongoDB as a data store, and has functionality for storing processed events from a particular topic
  into the database.
* **Containerization**: To manage the applications and their environments, Docker is used.
* This essential tool provides repeatability and scalability, enabling the application and its environment to be defined as code.

Please note that for complete details on the specific implementation, it's recommended to refer to the codebase directly or contact the
original creators of the project.

> To see more about how this solution works, please check
> the [annex/diagram section](#diagram)

### Benefits of Event-Driven Architecture

* **Efficient Use of Resources**: Rather than waiting for a server response, applications can continue with other operations.
  The response is then processed when it arrives, making efficient use of resources.
* **Improved Responsiveness and Performance**: Because event-driven systems are asynchronous, they can handle requests with low latency as
  they don't have to wait for operations to complete before moving on to the next task.
* **Scalability**: Since the services operate independently, it is easier to scale them separately based on demand.
  This allows the system to handle high loads efficiently.
* **Real-time updates**: Systems can react to changes as soon as they occur, providing real-time responsiveness, a feature that is crucial
  for many modern applications.
* **Fault Isolation**: In a loosely coupled event-driven architecture, if a service fails, it does not affect the entire system.
  It's also easier to identify and isolate the failure.
* **Decoupling**: Event-driven systems are typically loosely coupled, meaning changes to one part or service won't immediately affect
  others.
  This minimizes the impact of changes in the system and makes them easier to manage.
* **Flexibility**: It's easier to add, modify, or remove components or services from an event-driven architecture due to its loose coupling
  nature.
  This allows for easier system growth and evolution over time.
* **Reactivity**: Event-driven architectures are designed around the idea of reacting to events rather than polling for changes, leading to
  more efficient and timely communication.

### Use Case & Intended Purpose

Which teams are the target for this type of solution?

Teams that:

* require bulk actions but synchronous processes are time-consuming and resource expensive.
* require communication between scalable applications.
* require real-time processing.
* would like to provide a seamless and blazing-fast experience to their customers.
* would like to use Pub/Sub architecture with open-source technology.

---

## Build and Local Deploy

To run this demo, you'll need to have docker installed, see [Install Docker Engine](https://docs.docker.com/engine/install/) if it's not
installed.

Once Docker is figured out, verify that you're in the root directory of this repository `reactive-streams-demo-v2`, then, type the
following command.

```shell
$ docker-compose up
```

Once all the services are up, you can check the WebApp clicking over this [localhost link](http://localhost:8083/)

To create and send a message, click over ***"Publish a message!"*** or go to this
[localhost link](http://localhost:8083/publish-event-view), then follow the next steps

1. Enter a ***Message body***
2. Click over ***Create Message!*** button
3. Create as many messages as you want
4. Once messages are created, hit the ***Send Messages!*** button

You'll see a notification pop-up in the top part of the screen, and that's a message that has been transformed, stored and notified from
multiple microservices in a non-blocking way!

---

## Onboarding Guide

### Topic Subscriber

Create the following interfaces and class implementations and modify as needed.

```java
// TopicSubscriberService.java
public interface TopicSubscriberService<EventPayload> {
    Flux<MessageResult<Void>> subscribe(Flux<Message<EventPayload>> eventFlux);
}


// CustomTopicSubscriberService.java 
public interface CustomTopicSubscriberService extends TopicSubscriberService<String> {
}

// CustomTopicSubscriberServiceImpl.java 
@Service
public class CustomTopicSubscriberServiceImpl implements CustomTopicSubscriberService {

    // This is a placeholder for a publishing service
    private final PublisherService publisherService;

    @Autowired
    public CustomTopicSubscriberServiceImpl(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Override
    @ReactivePulsarListener(
            schemaType = SchemaType.STRING,
            subscriptionName = "custom-topic-subscription",
            stream = true,
            topics = "custom-topic"
    )
    public Flux<MessageResult<Void>> subscribe(Flux<Message<String>> eventFlux) {
        // Sink for Message Acknowledgement
        Sinks.Many<Message<String>> eventSink = Sinks.many().multicast().onBackpressureBuffer();

        // This will extract the messages from the topic event message wrapper
        var extractedEvents = eventFlux
                .doOnNext(
                        eventMessage ->
                                eventSink.tryEmitNext(eventMessage).orThrow()
                )
                .map(Message::getValue);

        // This will publish new events into an example of a wrapped local Hot Stream Service for another topic
        var publishedEventsFromNotifyStoredEventTopic = publisherService.publish(extractedEvents);

        // On subscription, the published events are going to be ignored, and will pass on to the eventSink to publish the
        // acknowledgements for the broker to mark the messages as acknowledged
        return publishedEventsFromNotifyStoredEventTopic.thenMany(eventSink.asFlux().map(MessageResult::acknowledge));
    }
}
```

> You might be able to follow a better example of this code in `pub-sub-web-app`
> at `/src/main/java/org/aaj/example/publisherwebapp/backend/service/NotifyStoredEventTopicSubscriberServiceImpl.java`

### Topic Publisher

Create the following interfaces and class implementations and modify as needed.

```java
// TopicPublisherService.java
public interface TopicPublisherService<EventToBeSent> {
    Flux<MessageSendResult<EventToBeSent>> publish(Flux<EventToBeSent> eventToBeSentFlux);
}

// CustomTopicPublisherService.java
public interface CustomTopicPublisherService extends TopicPublisherService<String> {
}

// CustomTopicPublisherServiceImpl.java
@Service
@Log4j2
public class CustomTopicPublisherServiceImpl implements CustomTopicPublisherService {

    private final ReactivePulsarTemplate<String> reactivePulsarTemplate;
    private static final String TOPIC_NAME = "custom-topic";

    @Autowired
    public CustomTopicPublisherServiceImpl(ReactivePulsarTemplate<String> reactivePulsarTemplate) {
        this.reactivePulsarTemplate = reactivePulsarTemplate;
    }

    @Override
    public Flux<MessageSendResult<String>> publish(Flux<String> eventToBeSentFlux) {
        return reactivePulsarTemplate
                .send(TOPIC_NAME, eventToBeSentFlux.map(MessageSpec::of))
                .onErrorResume(throwable -> {
                    log.error(throwable);
                    return Flux.just();
                });
    }
}
```

> You might be able to follow a better example of this code in `message-storer`
> at `/src/main/java/org/aaj/example/messagestorer/service/NotifyStoredEventTopicPublisherServiceImpl.java`

### Topic Creation

Create the following `@Configuration` annotated class to create topics

```java
// PulsarConfiguration.java
@Configuration
public class PulsarConfiguration {

    @Bean
    PulsarTopic customTopic() {
        // This will create a topic named "custom-topic" on the public namespace
        return PulsarTopic.builder("custom-topic").build();
    }

}
```

> You might be able to follow a better example of this code in `pulsar-administrator`
> at `/src/main/java/org/aaj/example/pulsaradministrator/config/PulsarConfiguration.java`

---

# Community Engagement

We welcome your contributions and involvement in the development of `reactive-streams-demo-v2`. Here are some ways you can participate:

- **Issue Tracker**: Found a bug or have a feature request?
  Submit an issue on our [GitHub Issue Tracker](https://github.com/Araya2001/reactive-streams-demo-v2/issues).
  When submitting a bug report, please include enough details to reproduce the bug.

- **Contribute Code**: Fork this repository, enhance the existing features or introduce new ones and
  [Submit a Pull Request](https://github.com/Araya2001/reactive-streams-demo-v2/pulls) with your changes.
  Remember to update the documentation, if necessary.

- **Star Our Project**: Enjoy using `reactive-streams-demo-v2`?
  Give it a star on GitHub!
  This helps increase its visibility and attract more contributors.

Every meaningful contribution is appreciated, whether they're feature enhancements, bug fixes, improvements to the code or documentation.

Thank you for being part of our project. We look forward to your contributions!

---

## Annex

### Diagram

![](annexes/diagram/reactive-streams-demo-v2.svg "Demo Diagram")

### Applied technologies in this solution

* [Apache Pulsar](https://pulsar.apache.org/)
* [MongoDB](https://www.mongodb.com/)
* [Docker](https://www.docker.com/get-started/)
* [Temurin Java](https://adoptium.net/temurin/releases/)
* [Reactive Spring](https://spring.io/reactive)
* [Vaadin](https://vaadin.com/)