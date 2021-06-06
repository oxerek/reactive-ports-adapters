package pl.oxerek.reactiveportsadapters.domain.ports;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Command<T> {

    Mono<T> execute();
}
