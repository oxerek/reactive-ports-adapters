package pl.oxerek.reactiveportsadapters.domain;

import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ArrayUtils;
import pl.oxerek.reactiveportsadapters.domain.ports.Repository;
import pl.oxerek.reactiveportsadapters.domain.ports.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Builder
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
class Payment {

    UUID id;

    Amount amount;

    User user;

    Account targetAccountNumber;

    Mono<Payment> save(Repository<PaymentDto> repository) {
        if (id != null) {
            return repository.update(toDto()).map(Payment::ofDto);
        }
        return repository.create(toDto()).map(paymentDto -> id(paymentDto.id().orElseThrow()));
    }

    Mono<Payment> mergeWith(Repository<PaymentDto> repository, Payment payment) {
        amount = amount.mergeWith(payment.amount);
        user = user.mergeWith(payment.user);
        targetAccountNumber = targetAccountNumber.mergeWith(payment.targetAccountNumber);

        return save(repository);
    }

    Mono<Payment> delete(Repository<PaymentDto> repository) {
        return repository.delete(id).map(Payment::ofDto);
    }

    PaymentDto toDto() {
        return PaymentDto.of(
              id,
              amount.value(),
              amount.currency().name(),
              user.id(),
              targetAccountNumber.number()
        );
    }

    static Mono<Payment> retrieveOne(Repository<PaymentDto> repository, UUID id) {
        return repository.findById(id).map(Payment::ofDto);
    }

    static Flux<Payment> retrieveMany(Repository<PaymentDto> repository, UUID... ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return repository.findAll().map(Payment::ofDto);
        }
        return repository.findAll(Set.of(ids)).map(Payment::ofDto);
    }

    static Payment ofDto(PaymentDto paymentDto) {
        return Payment.builder()
              .id(paymentDto.id().orElse(null))
              .amount(Amount.of(paymentDto.getAmount(), Currency.from(paymentDto.getCurrency())))
              .targetAccountNumber(Account.of(paymentDto.getTargetAccountNumber()))
              .user(User.of(paymentDto.getUserId()))
              .build();
    }
}
