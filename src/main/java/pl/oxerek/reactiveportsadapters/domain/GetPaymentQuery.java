package pl.oxerek.reactiveportsadapters.domain;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Query;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Mono;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetPaymentQuery implements Query<PaymentDto> {

    Repository<PaymentDto> repository;

    UUID id;

    @Override
    public Mono<PaymentDto> execute() {
        return Payment.retrieveOne(repository, id)
              .map(Payment::toDto);
    }
}
