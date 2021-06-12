package pl.oxerek.reactiveportsadapters.domain

import spock.lang.Shared

import static pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto.of
import static reactor.test.StepVerifier.create

class ModifyCommandSpec extends DomainBaseSpec {

    def "should modify existing payment #field"() {

        given:
        def paymentDto = of(BigDecimal.TEN, "PLN", UUID.randomUUID(), "PL12345678901234567890123456")
        def createdPaymentId = createPaymentAndReturnId(paymentDto)

        def command = modifyCommand(toModifyPayment, createdPaymentId)

        when:
        def stepVerifier = create(command.execute())
                .expectNextMatches(assertion)
                .expectComplete()

        then:
        stepVerifier.verify()

        where:
        field                   | toModifyPayment                       | assertion
        "amount"                | toModifyPaymentAmount                 | assertAmount(toModifyPaymentAmount)
        "currency"              | toModifyPaymentCurrency               | assertCurrency(toModifyPaymentCurrency)
        "userId"                | toModifyPaymentUserId                 | assertUserId(toModifyPaymentUserId)
        "targetAccountNumber"   | toModifyPaymentTargetAccountNumber    | assertTargetAccountNumber(toModifyPaymentTargetAccountNumber)
    }

    @Shared def toModifyPaymentAmount = of(null, new BigDecimal("234.48"), null, null, null)

    @Shared def toModifyPaymentCurrency = of(null, null, "EUR", null, null)

    @Shared def toModifyPaymentUserId = of(null, null, null, UUID.randomUUID(), null)

    @Shared def toModifyPaymentTargetAccountNumber = of(null, null, null, null, "DE12345678901234567890123456")

    static def assertAmount(toModifyPayment) {
        (modifiedPaymentDto) -> modifiedPaymentDto.getAmount() == toModifyPayment.getAmount()
    }

    static def assertCurrency(toModifyPayment) {
        (modifiedPaymentDto) -> modifiedPaymentDto.getCurrency() == toModifyPayment.getCurrency()
    }

    static def assertUserId(toModifyPayment) {
        (modifiedPaymentDto) -> modifiedPaymentDto.getUserId() == toModifyPayment.getUserId()
    }

    static def assertTargetAccountNumber(toModifyPayment) {
        (modifiedPaymentDto) -> modifiedPaymentDto.getTargetAccountNumber() == toModifyPayment.getTargetAccountNumber()
    }
}
