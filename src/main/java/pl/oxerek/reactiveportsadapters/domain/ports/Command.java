package pl.oxerek.reactiveportsadapters.domain.ports;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Command<D, R> {

    Mono<R> execute(D data);
}
