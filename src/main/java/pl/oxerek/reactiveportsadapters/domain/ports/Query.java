package pl.oxerek.reactiveportsadapters.domain.ports;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Query<D, R> {

    Mono<R> execute(D data);
}
