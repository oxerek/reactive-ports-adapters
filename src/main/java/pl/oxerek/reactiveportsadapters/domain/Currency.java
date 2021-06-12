package pl.oxerek.reactiveportsadapters.domain;

import static java.util.Arrays.stream;

enum Currency {
    PLN, USD, EUR;

    static Currency from(String currency) {
        return stream(values())
              .filter(value -> value.name().equals(currency))
              .findFirst()
              .orElse(null);
    }
}
