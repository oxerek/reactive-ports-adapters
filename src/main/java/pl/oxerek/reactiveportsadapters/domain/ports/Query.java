package pl.oxerek.reactiveportsadapters.domain.ports;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Query<T> {

    Mono<T> execute();
}
