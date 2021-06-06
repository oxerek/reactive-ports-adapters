package pl.oxerek.reactiveportsadapters.domain

import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class GetPaymentsSpec extends DomainBaseSpec {

    def "should retrieve all existing payments"() {

        given:
        def paymentDto1 = of(new BigDecimal("10.01"), "PLN", UUID.randomUUID(), "PL12345678901234567890123456")
        def paymentDto2 = of(new BigDecimal("13.21"), "USD", UUID.randomUUID(), "BE12345678901234567890123456")
        def paymentDto3 = of(new BigDecimal("384.24"), "EUR", UUID.randomUUID(), "PL12345678901234567890123456")
        def paymentDto4 = of(new BigDecimal("11.99"), "PLN", UUID.randomUUID(), "FR12345678901234567890123456")
        def paymentDto5 = of(new BigDecimal("8876.26"), "USD", UUID.randomUUID(), "DE12345678901234567890123456")

        storeAndSumPaymentsBatch.execute(List.of(paymentDto1, paymentDto2, paymentDto3, paymentDto4, paymentDto5)).block()

        expect:
        !store.isEmpty()
        store.size() == 5

        when:
        def stepVerifier = create(getPayments.execute(Set.of()))
                .expectNextCount(5)
                .expectComplete()

        then:
        stepVerifier.verify()
    }

    def "should retrieve existing payments with given ids"() {

        given:
        def paymentDto1 = of(new BigDecimal("10.01"), "PLN", UUID.randomUUID(), "PL12345678901234567890123456")
        def paymentDto2 = of(new BigDecimal("13.21"), "USD", UUID.randomUUID(), "BE12345678901234567890123456")
        def paymentDto3 = of(new BigDecimal("384.24"), "EUR", UUID.randomUUID(), "PL12345678901234567890123456")
        def paymentDto4 = of(new BigDecimal("11.99"), "PLN", UUID.randomUUID(), "FR12345678901234567890123456")
        def paymentDto5 = of(new BigDecimal("8876.26"), "USD", UUID.randomUUID(), "DE12345678901234567890123456")

        createOrModifyPaymentSpec.execute(paymentDto1).block().id().orElseThrow()
        createOrModifyPaymentSpec.execute(paymentDto3).block().id().orElseThrow()

        def createdPayment2Id = createOrModifyPaymentSpec.execute(paymentDto2).block().id().orElseThrow()
        def createdPayment4Id = createOrModifyPaymentSpec.execute(paymentDto4).block().id().orElseThrow()
        def createdPayment5Id = createOrModifyPaymentSpec.execute(paymentDto5).block().id().orElseThrow()

        expect:
        !store.isEmpty()
        store.size() == 5

        when:
        def stepVerifier = create(getPayments.execute(Set.of(createdPayment2Id, createdPayment4Id, createdPayment5Id)))
                .expectNext(of(paymentDto2, createdPayment2Id))
                .expectNext(of(paymentDto4, createdPayment4Id))
                .expectNext(of(paymentDto5, createdPayment5Id))
                .expectComplete()

        then:
        stepVerifier.verify()
    }
}
