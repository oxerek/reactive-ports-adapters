package pl.oxerek.reactiveportsadapters.domain.ports.dto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    UUID id;

    BigDecimal amount;

    String currency;

    UUID userId;

    String targetAccountNumber;

    public Optional<UUID> id() {
        return Optional.ofNullable(id);
    }

    public static PaymentDto of(BigDecimal amount, String currency, UUID userId, String targetAccountNumber) {
        return new PaymentDto(null, amount, currency, userId, targetAccountNumber);
    }

    public static PaymentDto of(UUID id, BigDecimal amount, String currency, UUID userId, String targetAccountNumber) {
        return new PaymentDto(id, amount, currency, userId, targetAccountNumber);
    }

    public static PaymentDto of(PaymentDto other, UUID id) {
        return new PaymentDto(id, other.amount, other.currency, other.userId, other.targetAccountNumber);
    }
}
