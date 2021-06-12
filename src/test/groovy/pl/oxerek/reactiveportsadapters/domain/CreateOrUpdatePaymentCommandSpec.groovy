package pl.oxerek.reactiveportsadapters.domain


import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class CreateOrUpdatePaymentCommandSpec extends DomainBaseSpec {

    def "should save new payment with generated id"() {

        given:
        def paymentDto = of(BigDecimal.TEN, "PLN", UUID.randomUUID(), "PL12345678901234567890123456")

        def createCommand = createOrUpdateCommand(paymentDto)

        when:
        def stepVerifier = create(createCommand.execute())
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
        def createdPaymentId = createPaymentAndReturnId(paymentDto)
        def toUpdatePayment = of(createdPaymentId, BigDecimal.ONE, "EUR", UUID.randomUUID(), "DE12345678901234567890123456")

        def updateCommand = createOrUpdateCommand(toUpdatePayment)

        expect:
        !store.isEmpty()
        store.size() == 1

        when:
        def stepVerifier = create(updateCommand.execute())
                .expectNextMatches(updatedPaymentDto -> updatedPaymentDto == toUpdatePayment)
                .expectComplete()

        then:
        stepVerifier.verify()
    }
}
