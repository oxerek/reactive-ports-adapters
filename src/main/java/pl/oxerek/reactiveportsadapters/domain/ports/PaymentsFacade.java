package pl.oxerek.reactiveportsadapters.domain.ports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.CreateOrModifyPaymentCommand;
import pl.oxerek.reactiveportsadapters.domain.DeletePaymentCommand;
import pl.oxerek.reactiveportsadapters.domain.GetPaymentQuery;
import pl.oxerek.reactiveportsadapters.domain.GetPaymentsQuery;
import pl.oxerek.reactiveportsadapters.domain.StoreAndSumPaymentsBatchCommand;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentsFacade {

    Repository<PaymentDto> repository;

    public Mono<PaymentDto> createOrModifyPayment(PaymentDto paymentDto) {
        var command = CreateOrModifyPaymentCommand.builder()
              .repository(repository)
              .paymentDto(paymentDto)
              .build();

        return command.execute();
    }

    public Mono<Void> deletePayment(UUID id) {
        var command = DeletePaymentCommand.builder()
              .repository(repository)
              .id(id)
              .build();

        return command.execute();
    }

    public Mono<PaymentDto> getPayment(UUID id) {
        var query = GetPaymentQuery.builder()
              .repository(repository)
              .id(id)
              .build();

        return query.execute();
    }

    public Flux<PaymentDto> getPayments(Set<UUID> ids) {
        var query = GetPaymentsQuery.builder()
              .repository(repository)
              .ids(ids)
              .build();

        return query.execute();
    }

    public Mono<BigDecimal> storeAndSumPaymentsBatch(List<PaymentDto> paymentsBatch) {
        var command = StoreAndSumPaymentsBatchCommand.builder()
              .repository(repository)
              .data(paymentsBatch)
              .build();

        return command.execute();
    }
}
