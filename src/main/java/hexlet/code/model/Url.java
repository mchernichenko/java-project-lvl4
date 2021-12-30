package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class Url extends Model {

    @Id
    //@Identity(generated = IdentityGenerated.BY_DEFAULT)
    private long id;

    @NotNull
    private String name;

    @WhenCreated
    private Instant createdAt;
}
