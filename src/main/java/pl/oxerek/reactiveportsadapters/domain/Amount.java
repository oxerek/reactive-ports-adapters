package pl.oxerek.reactiveportsadapters.domain;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
class Amount {

    BigDecimal value;

    Currency currency;

    String currency() {
        return currency != null ? currency.name() : null;
    }

    Amount mergeWith(Amount amount) {
        var modifyValue = amount.value != null && !Objects.equals(value, amount.value);
        var modifyCurrency = amount.currency != null && !Objects.equals(currency, amount.currency);

        var newValue = modifyValue ? amount.value : value;
        var newCurrency = modifyCurrency ? amount.currency : currency;

        return (modifyValue || modifyCurrency) ? of(newValue, newCurrency) : this;
    }
}
