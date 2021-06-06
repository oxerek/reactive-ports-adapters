package pl.oxerek.reactiveportsadapters.domain;

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
public class CreateOrModifyPaymentCommand implements Command<PaymentDto> {

    Repository<PaymentDto> repository;

    PaymentDto paymentDto;

    @Override
    public Mono<PaymentDto> execute() {
        return Payment.ofDto(paymentDto)
              .save(repository)
              .map(Payment::toDto);
    }
}
