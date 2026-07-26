package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import java.util.Arrays;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

public class UsersDbClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
  private final UserdataUserDao udUserDao = new UserdataUserDaoJdbc();

  private final AuthUserDao authUserDaoSpring = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDaoSpring = new AuthAuthorityDaoSpringJdbc();
  private final UserdataUserDao udUserDaoSpring = new UserdataUserDaoSpringJdbc();

  private final TransactionTemplate chainedTxTemplate = new TransactionTemplate(
      new ChainedTransactionManager(
          new JdbcTransactionManager(
              DataSources.dataSource(CFG.authJdbcUrl())
          ),
          new JdbcTransactionManager(
              DataSources.dataSource(CFG.userdataJdbcUrl())
          )
      )
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  private final TransactionTemplate txTemplate = new TransactionTemplate(
      new JdbcTransactionManager(
          DataSources.dataSource(CFG.authJdbcUrl())
      )
  );

  public UserJson createUserSpringJdbcChainedTx(UserJson user) {
    return chainedTxTemplate.execute(status -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(user.username());
      authUser.setPassword(pe.encode("12345"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);

      authUserDaoSpring.create(authUser);

      AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
          authority -> {
            AuthorityEntity userAuthority = new AuthorityEntity();
            userAuthority.setAuthority(authority);
            userAuthority.setUserId(authUser.getId());
            return userAuthority;
          }
      ).toArray(AuthorityEntity[]::new);

      authAuthorityDaoSpring.create(authorityEntities);
      return UserJson.fromEntity(
          udUserDaoSpring.create(UserEntity.fromJson(user)),
          null
      );
    });
  }

  public UserJson createUserSpringJdbcXaTx(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(user.username());
      authUser.setPassword(pe.encode("12345"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);

      authUserDaoSpring.create(authUser);

      AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
          authority -> {
            AuthorityEntity userAuthority = new AuthorityEntity();
            userAuthority.setAuthority(authority);
            userAuthority.setUserId(authUser.getId());
            return userAuthority;
          }
      ).toArray(AuthorityEntity[]::new);

      authAuthorityDaoSpring.create(authorityEntities);
      return UserJson.fromEntity(
          udUserDaoSpring.create(UserEntity.fromJson(user)),
          null
      );
    });
  }

  public UserJson createUserSpringJdbc(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);

    AuthUserEntity createdAuthUser = authUserDaoSpring.create(authUser);

    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
        e -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setUserId(createdAuthUser.getId());
          ae.setAuthority(e);
          return ae;
        }
    ).toArray(AuthorityEntity[]::new);

    authAuthorityDaoSpring.create(authorityEntities);

    return UserJson.fromEntity(
        udUserDaoSpring.create(UserEntity.fromJson(user)),
        null
    );
  }

  @Override
  public UserJson createUser(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
          AuthUserEntity authUser = new AuthUserEntity();
          authUser.setUsername(user.username());
          authUser.setPassword(pe.encode("12345"));
          authUser.setEnabled(true);
          authUser.setAccountNonExpired(true);
          authUser.setAccountNonLocked(true);
          authUser.setCredentialsNonExpired(true);

          AuthUserEntity createdAuthUser = authUserDao.create(authUser);

          AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
              e -> {
                AuthorityEntity ae = new AuthorityEntity();
                ae.setUserId(createdAuthUser.getId());
                ae.setAuthority(e);
                return ae;
              }
          ).toArray(AuthorityEntity[]::new);

          authAuthorityDao.create(authorityEntities);
          return UserJson.fromEntity(
              udUserDao.create(UserEntity.fromJson(user)),
              null
          );
        }
    );
  }

  public UserJson createUserJdbc(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);

    authUserDao.create(authUser);

    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
        authority -> {
          AuthorityEntity userAuthority = new AuthorityEntity();
          userAuthority.setAuthority(authority);
          userAuthority.setUserId(authUser.getId());
          return userAuthority;
        }
    ).toArray(AuthorityEntity[]::new);

    authAuthorityDao.create(authorityEntities);
    return UserJson.fromEntity(
        udUserDao.create(UserEntity.fromJson(user)),
        null
    );
  }
}
