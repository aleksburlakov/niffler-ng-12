package guru.qa.niffler.service;

import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.AuthUserJson;
import java.util.List;

public interface UserClient {

  AuthUserJson createAuthUser(AuthUserJson user, List<AuthAuthorityJson> authorities);
}
