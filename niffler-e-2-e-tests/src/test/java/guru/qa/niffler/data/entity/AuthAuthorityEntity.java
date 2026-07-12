package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.AuthAuthorityJson;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthAuthorityEntity implements Serializable {
  private UUID id;
  private Authority authority;
  private AuthUserEntity user;

  public static AuthAuthorityEntity fromJson(AuthAuthorityJson json) {
    AuthAuthorityEntity entity = new AuthAuthorityEntity();
    entity.setAuthority(json.authority());
    entity.setUser(AuthUserEntity.fromJson(json.user()));
    return entity;
  }

  public enum Authority {
    read, write
  }
}
