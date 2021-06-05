package pl.oxerek.reactiveportsadapters.domain;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Command;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StoreAndSumPaymentsBatch implements Command<List<PaymentDto>, BigDecimal> {

    Repository<PaymentDto> repository;

    @Override
    public Mono<BigDecimal> execute(List<PaymentDto> data) {
        return Flux.just(data.toArray(PaymentDto[]::new))
              .flatMap(paymentDto -> Payment.ofDto(paymentDto).save(repository))
              .map(payment -> payment.amount().value())
              .reduce(BigDecimal::add);
    }
}
