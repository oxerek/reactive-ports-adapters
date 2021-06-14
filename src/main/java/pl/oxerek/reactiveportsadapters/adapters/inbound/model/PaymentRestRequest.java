package pl.oxerek.reactiveportsadapters.adapters.inbound.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Value;

@Value
public class PaymentRestRequest {

    BigDecimal amount;

    String currency;

    UUID userId;

    String targetAccountNumber;
}
