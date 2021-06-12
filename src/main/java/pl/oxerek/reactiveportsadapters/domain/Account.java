package pl.oxerek.reactiveportsadapters.domain;

import java.util.Objects;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
class Account {

    String number;

    Account mergeWith(Account account) {
        return account.number == null || Objects.equals(number, account.number) ? this : of(account.number);
    }
}
