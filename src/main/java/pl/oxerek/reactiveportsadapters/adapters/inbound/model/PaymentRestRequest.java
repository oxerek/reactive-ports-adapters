package pl.oxerek.reactiveportsadapters.adapters.inbound.model;

import java.math.BigDecimal;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class PaymentRestRequest {

    @NotNull
    BigDecimal amount;

    @NotBlank
    String currency;

    @NotNull
    UUID userId;

    @NotBlank
    String targetAccountNumber;
}
