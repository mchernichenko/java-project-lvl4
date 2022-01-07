package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

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
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "url",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL)
    @OrderBy("createdAt desc")
    private List<UrlCheck> urlChecks;
}
