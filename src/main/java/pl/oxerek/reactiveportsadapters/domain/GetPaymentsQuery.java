package pl.oxerek.reactiveportsadapters.domain;

import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.StreamedQuery;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetPaymentsQuery implements StreamedQuery<PaymentDto> {

    Repository<PaymentDto> repository;

    @Builder.Default
    Set<UUID> ids = Set.of();

    @Override
    public Flux<PaymentDto> execute() {
        return Payment.retrieveMany(repository, ids.toArray(UUID[]::new))
              .map(Payment::toDto);
    }
}
