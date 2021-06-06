package pl.oxerek.reactiveportsadapters.domain

import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class GetPaymentQuerySpec extends DomainBaseSpec {

    def "should retrieve existing payment with given id"() {

        given:
        def paymentDto = of(BigDecimal.TEN, "PLN", UUID.randomUUID(), "PL12345678901234567890123456")
        def createdPaymentId = createPaymentAndReturnId(paymentDto)

        def query = getQuery(createdPaymentId)

        expect:
        !store.isEmpty()
        store.size() == 1

        when:
        def stepVerifier = create(query.execute())
                .expectNextMatches(retrievedPayment -> retrievedPayment == of(paymentDto, createdPaymentId))
                .expectComplete()

        then:
        stepVerifier.verify()
    }
}
