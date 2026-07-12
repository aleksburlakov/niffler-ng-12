package guru.qa.niffler.service;

import static guru.qa.niffler.data.Databases.transaction;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.AuthUserJson;
import java.sql.Connection;
import java.util.List;

public class UserDbClient implements UserClient {

  private static final Config CFG = Config.getInstance();
  private static final int ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;


  @Override
  public AuthUserJson createAuthUser(AuthUserJson user, List<AuthAuthorityJson> authorities) {
    return transaction(
        ISOLATION_LEVEL,
        connection -> {
          AuthUserEntity authUserEntity = AuthUserEntity.fromJson(user);
          AuthUserEntity createdUser = new AuthUserDaoJdbc(connection).create(authUserEntity);

          List<AuthAuthorityEntity> authorityEntities = authorities
              .stream()
              .map(
                  authAuthorityJson -> {
                    AuthAuthorityEntity authAuthorityEntity = AuthAuthorityEntity.fromJson(authAuthorityJson);
                    authAuthorityEntity.setUser(createdUser);
                    return authAuthorityEntity;
                  })
              .toList();
          new AuthAuthorityDaoJdbc(connection).create(authorityEntities);
          return AuthUserJson.fromEntity(authUserEntity);
        },
        CFG.authJdbcUrl()
    );
  }
}
