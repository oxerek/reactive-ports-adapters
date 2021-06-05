package pl.oxerek.reactiveportsadapters.domain.ports;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface StreamedQuery<D, R> {

    Flux<R> execute(D data);
}
