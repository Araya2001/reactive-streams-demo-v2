# Reactive Streams Demo V2

    Author: Alejandro Araya Jiménez
    Create Date: 2024-07-06
    Update Date: 2024-07-11

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

You'll see a notification pop-up in the top part of the screen and that's a message that has been transformed, stored and notified from 
multiple microservices and in a non-blocking way!

---

## How it was built

This demo is currently using the following tech: 
* [Apache Pulsar](https://pulsar.apache.org/)
* [MongoDB](https://www.mongodb.com/)
* [Docker](https://www.docker.com/get-started/)
* [Temurin Java](https://adoptium.net/temurin/releases/)
* [Reactive Spring](https://spring.io/reactive)
* [Vaadin](https://vaadin.com/)


This containerized demo is currently using `Dockerfiles` for spring containers.
`Docker Compose` is being used to build the spring container images,
to get up and running the `MongoDB` local instance and create the `Pulsar` cluster.

---

## Annexes

### Diagram

![](annexes/diagram/reactive-streams-demo-v2.svg "Demo Diagram")

