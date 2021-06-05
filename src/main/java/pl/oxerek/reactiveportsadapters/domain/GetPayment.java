package pl.oxerek.reactiveportsadapters.domain;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Query;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetPayment implements Query<UUID, PaymentDto> {

    Repository<PaymentDto> repository;

    @Override
    public Mono<PaymentDto> execute(UUID id) {
        return Payment.retrieveOne(repository, id)
              .map(Payment::toDto);
    }
}
