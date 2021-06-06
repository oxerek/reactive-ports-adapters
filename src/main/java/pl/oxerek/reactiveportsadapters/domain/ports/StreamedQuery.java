package pl.oxerek.reactiveportsadapters.domain.ports;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface StreamedQuery<T> {

    Flux<T> execute();
}
