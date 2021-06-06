package pl.oxerek.reactiveportsadapters.domain

import pl.oxerek.reactiveportsadapters.adapters.outbound.PaymentInMemoryRepository
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentInMemoryEntity
import spock.lang.Specification

class DomainBaseSpec extends Specification {

    def store = [:] as Map<UUID, PaymentInMemoryEntity>

    def paymentRepository = new PaymentInMemoryRepository(store)

    def createOrModifyPaymentSpec = new CreateOrModifyPayment(paymentRepository)

    def deletePayment = new DeletePayment(paymentRepository)

    def getPayment = new GetPayment(paymentRepository)

    def getPayments = new GetPayments(paymentRepository)

    def storeAndSumPaymentsBatch = new StoreAndSumPaymentsBatch(paymentRepository)
}
