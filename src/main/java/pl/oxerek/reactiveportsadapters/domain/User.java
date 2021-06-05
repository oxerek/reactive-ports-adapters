package pl.oxerek.reactiveportsadapters.domain;

import java.util.UUID;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
class User {

    UUID id;
}
