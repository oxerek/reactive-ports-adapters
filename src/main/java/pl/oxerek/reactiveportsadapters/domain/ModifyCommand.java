package pl.oxerek.reactiveportsadapters.domain;

import static pl.oxerek.reactiveportsadapters.domain.Payment.ofDto;

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
public class ModifyCommand implements Command<PaymentDto> {

    Repository<PaymentDto> repository;

    PaymentDto paymentDto;

    UUID id;

    @Override
    public Mono<PaymentDto> execute() {
        return repository.findById(id)
              .map(Payment::ofDto)
              .flatMap(payment -> payment.mergeWith(repository, ofDto(paymentDto)))
              .map(Payment::toDto);
    }
}
