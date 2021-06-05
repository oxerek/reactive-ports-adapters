package pl.oxerek.reactiveportsadapters.domain;

import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.StreamedQuery;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetPayments implements StreamedQuery<Set<UUID>, PaymentDto> {

    Repository<PaymentDto> repository;

    @Override
    public Flux<PaymentDto> execute(Set<UUID> ids) {
        return Payment.retrieveMany(repository, ids.toArray(UUID[]::new))
              .map(Payment::toDto);
    }
}
