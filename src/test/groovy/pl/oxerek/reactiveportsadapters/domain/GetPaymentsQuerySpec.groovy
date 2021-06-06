package pl.oxerek.reactiveportsadapters.domain


import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class GetPaymentsQuerySpec extends DomainBaseSpec {

    def "should retrieve all existing payments"() {

        given:
        createPaymentAndReturnId(of(new BigDecimal("10.01"), "PLN", UUID.randomUUID(), "PL12345678901234567890123456"))
        createPaymentAndReturnId(of(new BigDecimal("13.21"), "USD", UUID.randomUUID(), "BE12345678901234567890123456"))
        createPaymentAndReturnId(of(new BigDecimal("384.24"), "EUR", UUID.randomUUID(), "PL12345678901234567890123456"))
        createPaymentAndReturnId(of(new BigDecimal("11.99"), "PLN", UUID.randomUUID(), "FR12345678901234567890123456"))
        createPaymentAndReturnId(of(new BigDecimal("8876.26"), "USD", UUID.randomUUID(), "DE12345678901234567890123456"))

        def query = getQuery(Set.of())

        expect:
        !store.isEmpty()
        store.size() == 5

        when:
        def stepVerifier = create(query.execute())
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

        def createdPayment2Id = createPaymentAndReturnId(paymentDto2)
        def createdPayment4Id = createPaymentAndReturnId(paymentDto4)
        def createdPayment5Id = createPaymentAndReturnId(paymentDto5)

        createPaymentAndReturnId(paymentDto1)
        createPaymentAndReturnId(paymentDto3)

        def query = getQuery(Set.of(createdPayment2Id, createdPayment4Id, createdPayment5Id))

        expect:
        !store.isEmpty()
        store.size() == 5

        when:
        def stepVerifier = create(query.execute())
                .expectNext(of(paymentDto2, createdPayment2Id))
                .expectNext(of(paymentDto4, createdPayment4Id))
                .expectNext(of(paymentDto5, createdPayment5Id))
                .expectComplete()

        then:
        stepVerifier.verify()
    }
}
