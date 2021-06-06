package pl.oxerek.reactiveportsadapters.adapters.outbound.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class PaymentInMemoryEntity implements Serializable {

    UUID id;

    BigDecimal amount;

    String currency;

    UUID userId;

    String targetAccountNumber;
}
