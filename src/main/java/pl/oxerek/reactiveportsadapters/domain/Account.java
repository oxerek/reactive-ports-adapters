package pl.oxerek.reactiveportsadapters.domain;

import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
class Account {

    String number;
}
