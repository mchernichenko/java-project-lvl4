package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.Identity;
import io.ebean.annotation.IdentityGenerated;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Getter
// генерирует конструктор, принимающий значения для каждого final поля
@RequiredArgsConstructor
@Entity
public class Url extends Model {

    @Id
    //@Identity(generated = IdentityGenerated.BY_DEFAULT)
    private long id;

    @NotNull
    private final String name;

    @WhenCreated
    private Instant createdAt;
}
