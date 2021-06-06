package pl.oxerek.reactiveportsadapters.domain

import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class StoreAndSumPaymentsBatchSpec extends DomainBaseSpec {

    def "should store batch of payments and return sum of amounts"() {

        given:
        def amount1 = new BigDecimal("10.01")
        def amount2 = new BigDecimal("13.21")
        def amount3 = new BigDecimal("384.24")
        def amount4 = new BigDecimal("11.99")
        def amount5 = new BigDecimal("8876.26")

        def paymentsBatch = [
                of(amount1, "PLN", UUID.randomUUID(), "PL12345678901234567890123456"),
                of(amount2, "USD", UUID.randomUUID(), "BE12345678901234567890123456"),
                of(amount3, "EUR", UUID.randomUUID(), "PL12345678901234567890123456"),
                of(amount4, "PLN", UUID.randomUUID(), "FR12345678901234567890123456"),
                of(amount5, "USD", UUID.randomUUID(), "DE12345678901234567890123456")
        ]

        when:
        def stepVerifier = create(storeAndSumPaymentsBatch.execute(paymentsBatch))
                .expectNext(amount1.add(amount2).add(amount3).add(amount4).add(amount5))
                .expectComplete()

        then:
        stepVerifier.verify()

        !store.isEmpty()
        store.size() == 5
    }
}
