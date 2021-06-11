package pl.oxerek.reactiveportsadapters.adapters.inbound;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.oxerek.reactiveportsadapters.adapters.outbound.PaymentInMemoryRepository;
import pl.oxerek.reactiveportsadapters.domain.ports.PaymentsFacade;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.util.Loggers;

@NoArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DummyController {

    public static void main(String[] args) {
        Loggers.useConsoleLoggers();

        var paymentsFacade = new PaymentsFacade(new PaymentInMemoryRepository(new ConcurrentHashMap<>()));

        var paymentDto = PaymentDto.of(
              BigDecimal.valueOf(1123.33),
              "PLN",
              UUID.randomUUID(),
              "PL12345678901234567890123456"
        );

        var paymentDtoSaved = paymentsFacade.createOrUpdatePayment(paymentDto).log().block();

        System.out.println("Saved = " + paymentDtoSaved);

        var paymentDtoRetrieved = paymentsFacade.getPayment(paymentDtoSaved.id().orElseThrow()).log().block();

        System.out.println("Retrieved = " + paymentDtoRetrieved);

        var paymentDtoModified = PaymentDto.of(
              paymentDtoRetrieved.id().orElseThrow(),
              BigDecimal.valueOf(99.00),
              "PLN",
              UUID.randomUUID(),
              "PL12345678901234567890123456"
        );

        paymentsFacade.createOrUpdatePayment(paymentDtoModified).log().block();

        var paymentDtoRetrievedAfterModify = paymentsFacade.getPayment(paymentDtoModified.id().orElseThrow()).log().block();

        System.out.println("Modified = " + paymentDtoRetrievedAfterModify);

        paymentsFacade.deletePayment(paymentDtoRetrievedAfterModify.id().orElseThrow()).log().block();

        var paymentDtoRetrievedAfterDelete = paymentsFacade.getPayment(paymentDtoRetrievedAfterModify.id().orElseThrow()).log().block();

        System.out.println("Deleted = " + paymentDtoRetrievedAfterDelete);

        var paymentsBatch = new ArrayList<PaymentDto>();

        for (int i = 0; i < 10; i++) {
            paymentsBatch.add(PaymentDto.of(
                  BigDecimal.valueOf((i + 1) * 100),
                  "PLN",
                  UUID.randomUUID(),
                  "PL12345678901234567890123456"
            ));
        }

        var sum = paymentsFacade.storeAndSumPaymentsBatch(paymentsBatch).log().block();

        System.out.println("Sum = " + sum);

        var retrievedPaymentDtos = paymentsFacade.getPayments(Set.of()).collectList().log().block();

        System.out.println("RetrievedPaymentDtos = " + retrievedPaymentDtos);
    }
}
