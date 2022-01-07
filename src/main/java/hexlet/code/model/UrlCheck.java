package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.Identity;
import io.ebean.annotation.IdentityGenerated;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Entity;
import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
@Entity
public class UrlCheck extends Model {
    @Id
    @Identity(generated = IdentityGenerated.BY_DEFAULT)
    private long id;

    @NotNull
    private final int statusCode;

    private final String title;
    private final String h1;

    @Lob
    private final String description;

    @ManyToOne
    @JoinColumn(name = "url_id")
    private final Url url;

    @WhenCreated
    private ZonedDateTime createdAt;

  /*  public UrlCheck(int statusCode, String title, String h1, String description, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }*/
}
