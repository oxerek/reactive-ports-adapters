package pl.oxerek.reactiveportsadapters.adapters.outbound.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class PaymentInMemoryEntity {

    UUID id;

    BigDecimal amount;

    String currency;

    UUID userId;

    String targetAccountNumber;
}
