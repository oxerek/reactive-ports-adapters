package pl.oxerek.reactiveportsadapters.domain.ports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentsFacade {

    Command<PaymentDto, PaymentDto> createOrModifyPayment;

    Command<UUID, UUID> deletePayment;

    Command<List<PaymentDto>, BigDecimal> storeAndSumPaymentsBatch;

    Query<UUID, PaymentDto> getPayment;

    StreamedQuery<Set<UUID>, PaymentDto> getPayments;

    public Mono<PaymentDto> createOrModifyPayment(PaymentDto paymentDto) {
        return Mono.just(paymentDto).log().flatMap(createOrModifyPayment::execute);
    }

    public Mono<Void> deletePayment(UUID id) {
        return deletePayment.execute(id).then();
    }

    public Mono<BigDecimal> storeAndSumPaymentsBatch(List<PaymentDto> paymentsBatch) {
        return storeAndSumPaymentsBatch.execute(paymentsBatch);
    }

    public Mono<PaymentDto> getPayment(UUID id) {
        return getPayment.execute(id);
    }

    public Flux<PaymentDto> getPayments(Set<UUID> ids) {
        return getPayments.execute(ids);
    }
}
