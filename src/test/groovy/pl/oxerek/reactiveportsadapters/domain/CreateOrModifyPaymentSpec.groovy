package pl.oxerek.reactiveportsadapters.domain

import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class CreateOrModifyPaymentSpec extends DomainBaseSpec {

    def "should save new payment with generated id"() {

        given:
        def paymentDto = of(BigDecimal.TEN, "PLN", UUID.randomUUID(), "PL12345678901234567890123456")

        when:
        def stepVerifier = create(createOrModifyPaymentSpec.execute(paymentDto))
                .expectNextMatches(createdPaymentDto -> createdPaymentDto.id().isPresent())
                .expectComplete()

        then:
        stepVerifier.verify()

        !store.isEmpty()
        store.size() == 1
    }

    def "should create and then modify existing payment"() {

        given:
        def paymentDto = of(BigDecimal.TEN, "PLN", UUID.randomUUID(), "PL12345678901234567890123456")
        def createdPaymentId = createOrModifyPaymentSpec.execute(paymentDto).block().id().orElseThrow()
        def toUpdatePayment = of(createdPaymentId, BigDecimal.ONE, "EUR", UUID.randomUUID(), "DE12345678901234567890123456")

        expect:
        !store.isEmpty()
        store.size() == 1

        when:
        def stepVerifier = create(createOrModifyPaymentSpec.execute(toUpdatePayment))
                .expectNextMatches(modifiedPaymentDto -> modifiedPaymentDto == toUpdatePayment)
                .expectComplete()

        then:
        stepVerifier.verify()
    }
}
