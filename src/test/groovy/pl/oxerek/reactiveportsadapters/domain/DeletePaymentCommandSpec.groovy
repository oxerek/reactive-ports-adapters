package pl.oxerek.reactiveportsadapters.domain

import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class DeletePaymentCommandSpec extends DomainBaseSpec {

    def "should delete existing payment with given id"() {

        given:
        def paymentDto = of(BigDecimal.TEN, "PLN", UUID.randomUUID(), "PL12345678901234567890123456")
        def createdPaymentId = createPaymentAndReturnId(paymentDto)

        def deleteCommand = deleteCommand(createdPaymentId)

        expect:
        !store.isEmpty()
        store.size() == 1

        when:
        def stepVerifier = create(deleteCommand.execute()).expectComplete()

        then:
        stepVerifier.verify()

        store.isEmpty()
    }
}
