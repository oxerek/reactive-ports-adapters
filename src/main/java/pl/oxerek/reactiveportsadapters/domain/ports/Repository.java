package pl.oxerek.reactiveportsadapters.domain.ports;

import java.util.Set;
import java.util.UUID;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Repository<E> {

    Mono<E> findById(UUID id);

    Flux<E> findAll();

    Flux<E> findAll(Set<UUID> ids);

    Mono<E> create(E dto);

    Mono<E> update(E dto);

    Mono<PaymentDto> delete(UUID id);
}
