package pl.oxerek.reactiveportsadapters.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Command;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateOrModifyPayment implements Command<PaymentDto, PaymentDto> {

    Repository<PaymentDto> repository;

    @Override
    public Mono<PaymentDto> execute(PaymentDto paymentDto) {
        return Payment.ofDto(paymentDto)
              .save(repository)
              .map(Payment::toDto);
    }
}
