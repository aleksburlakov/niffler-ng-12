package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import java.util.UUID;

public record AuthAuthorityJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("authority")
    AuthAuthorityEntity.Authority authority,
    @JsonProperty("user_id")
    AuthUserJson user
) {
  public static AuthAuthorityJson fromEntity(AuthAuthorityEntity authAuthorityEntity) {
    return new AuthAuthorityJson(
        authAuthorityEntity.getId(),
        authAuthorityEntity.getAuthority(),
        AuthUserJson.fromEntity(authAuthorityEntity.getUser())
    );
  }
}
