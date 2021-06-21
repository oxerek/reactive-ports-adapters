# reactive-ports-adapters

Demo application written in reactive manner with Ports&Adapters architecture. 
CQRS pattern used as convenient way of invoking of domain functionalities.

The application shows integration with several Java related technologies as adapters while keeping the domain clean.
Main assumption: Mono and Flux from https://projectreactor.io/ are treated as a part of a language and can be used inside the domain.

Integrated adapters:
- webflux rest handler
- grpc reactive service
- Hazelcast inmemory object store
- Apache commons csv library

Extras:
- java_dev_excercise.pdf with description of app features
- reactive-ports-adapters.postman_collection.json

TODO:
- adapters tests
- integration tests
- block hound tests
- validation (grpc, rest, conf)
- transactions
- configuration + profiles + versions info in banner + zalando logger
- spring security
- graphql
- kafka streams
- docker compose (gusaul/grpcox, zookeeper, kafka, kafdrop)
