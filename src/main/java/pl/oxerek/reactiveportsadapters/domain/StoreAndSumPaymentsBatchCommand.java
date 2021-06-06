package pl.oxerek.reactiveportsadapters.domain;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.Command;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StoreAndSumPaymentsBatchCommand implements Command<BigDecimal> {

    Repository<PaymentDto> repository;

    List<PaymentDto> data;

    @Override
    public Mono<BigDecimal> execute() {
        return Flux.just(data.toArray(PaymentDto[]::new))
              .flatMap(paymentDto -> Payment.ofDto(paymentDto).save(repository))
              .map(payment -> payment.amount().value())
              .reduce(BigDecimal::add);
    }
}
