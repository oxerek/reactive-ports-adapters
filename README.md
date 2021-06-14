# reactive-ports-adapters

Demo application written in reactive manner with Ports&Adapters architecture. 
CQRS pattern used as convenient way of invoking of domain functionalities.

The application shows integration with several Java related technologies as adapters while keeping the domain clean.
Main assumption: Mono and Flux from https://projectreactor.io/ are treated as a part of a language.

Integrated adapters:
- webflux handler
- grpc reactive service
- inmemory object store as a simple map
- Hazelcast inmemory object store

Extras:
- java_dev_excercise.pdf with description of app features
- reactive-ports-adapters.postman_collection.json

TODO:
- csv repository
- adapters tests
- integration tests
- versions info in banner
- validation (grpc, rest)
- trasactions
- spring profiles
- configuration
- spring security
- graphql
- kafka streams
- docker compose (gusaul/grpcox, zookeeper, kafka, kafdrop)
