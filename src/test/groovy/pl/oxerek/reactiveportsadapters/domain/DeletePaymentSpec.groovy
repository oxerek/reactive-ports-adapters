package pl.oxerek.reactiveportsadapters.domain

import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class DeletePaymentSpec extends DomainBaseSpec {

    def "should delete existing payment with given id"() {

        given:
        def paymentDto = of(BigDecimal.TEN, "PLN", UUID.randomUUID(), "PL12345678901234567890123456")
        def createdPaymentId = createOrModifyPaymentSpec.execute(paymentDto).block().id().orElseThrow()

        expect:
        !store.isEmpty()
        store.size() == 1

        when:
        def stepVerifier = create(deletePayment.execute(createdPaymentId)).expectComplete()

        then:
        stepVerifier.verify()

        store.isEmpty()
    }
}
