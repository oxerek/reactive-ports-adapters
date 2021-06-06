package pl.oxerek.reactiveportsadapters.domain;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Command;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeletePayment implements Command<UUID, Void> {

    Repository<PaymentDto> repository;

    @Override
    public Mono<Void> execute(UUID id) {
        return Payment.retrieveOne(repository, id)
              .flatMap(payment -> payment.delete(repository))
              .then(Mono.empty());
    }
}
