package pl.oxerek.reactiveportsadapters.domain;

import java.util.Objects;
import java.util.UUID;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
class User {

    UUID id;

    User mergeWith(User user) {
        return Objects.equals(id, user.id) ? this : of(user.id);
    }
}
