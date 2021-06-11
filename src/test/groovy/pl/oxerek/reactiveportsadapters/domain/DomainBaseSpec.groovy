package pl.oxerek.reactiveportsadapters.domain

import pl.oxerek.reactiveportsadapters.adapters.outbound.PaymentInMemoryRepository
import pl.oxerek.reactiveportsadapters.adapters.outbound.model.PaymentInMemoryEntity
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto
import spock.lang.Specification

class DomainBaseSpec extends Specification {

    def store = [:] as Map<UUID, PaymentInMemoryEntity>

    def repository = new PaymentInMemoryRepository(store)

    def createPaymentAndReturnId(PaymentDto paymentDto) {
        CreateOrUpdatePaymentCommand.builder()
                .repository(repository)
                .paymentDto(paymentDto)
                .build()
                .execute().block().id().orElseThrow()
    }

    def createOrUpdateCommand(PaymentDto paymentDto) {
        CreateOrUpdatePaymentCommand.builder()
                .repository(repository)
                .paymentDto(paymentDto)
                .build()
    }

    def deleteCommand(UUID createdPaymentId) {
        DeletePaymentCommand.builder()
                .repository(repository)
                .id(createdPaymentId)
                .build()
    }

    def getQuery(UUID createdPaymentId) {
        GetPaymentQuery.builder()
                .repository(repository)
                .id(createdPaymentId)
                .build()
    }

    def getQuery(Set<UUID> ids) {
        GetPaymentsQuery.builder()
                .repository(repository)
                .ids(ids)
                .build()
    }

    def storeAndReturnSumCommand(ArrayList<PaymentDto> paymentsBatch) {
        StoreAndSumPaymentsBatchCommand.builder()
                .repository(repository)
                .data(paymentsBatch)
                .build()
    }
}
