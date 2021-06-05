package pl.oxerek.reactiveportsadapters.domain;

import java.math.BigDecimal;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
class Amount {

    BigDecimal value;

    Currency currency;
}
