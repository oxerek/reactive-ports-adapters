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

    Amount mergeWith(Amount amount) {
        var valuesNotEqual = !Objects.equals(value, amount.value);
        var currenciesNotEqual = !Objects.equals(currency, amount.currency);

        var newValue = valuesNotEqual ? amount.value : value;
        var newCurrency = currenciesNotEqual ? amount.currency : currency;

        return (valuesNotEqual || currenciesNotEqual) ? of(newValue, newCurrency) : this;
    }
}
