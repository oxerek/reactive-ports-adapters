package pl.oxerek.reactiveportsadapters.domain;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Command;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Mono;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeletePaymentCommand implements Command<Void> {

    Repository<PaymentDto> repository;

    UUID id;

    @Override
    public Mono<Void> execute() {
        return Payment.retrieveOne(repository, id)
              .flatMap(payment -> payment.delete(repository))
              .then(Mono.empty());
    }
}
