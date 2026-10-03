package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

  private static final Config CFG = Config.getInstance();
  private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    authUserDao.create(user);

    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
        e -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setUser(user);
          ae.setAuthority(e);
          return ae;
        }
    ).toArray(AuthorityEntity[]::new);
    authAuthorityDao.create(authorityEntities);

    return user;
  }

  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    return authUserDao.update(user);
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    return Optional.ofNullable(
        jdbcTemplate.query(
            "SELECT a.id AS authority_id, " +
                "a.authority, " +
                "u.id AS user_id, " +
                "u.username, " +
                "u.password, " +
                "u.enabled, " +
                "u.account_non_expired, " +
                "u.account_non_locked, " +
                "u.credentials_non_expired " +
                "FROM \"user\" u JOIN \"authority\" a ON u.id = a.user_id " +
                "WHERE u.id = ?",
            AuthUserExtractor.instance,
            id
        )
    );
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    return Optional.ofNullable(
        jdbcTemplate.query(
            "SELECT a.id AS authority_id, " +
                "authority, " +
                "user_id AS id, " +
                "u.username, " +
                "u.password, " +
                "u.enabled, " +
                "u.account_non_expired, " +
                "u.account_non_locked, " +
                "u.credentials_non_expired " +
                "FROM \"user\" u JOIN \"authority\" a ON u.id = a.user_id " +
                "WHERE u.username = ?",
            AuthUserExtractor.instance,
            username
        )
    );
  }

  @Override
  public void remove(AuthUserEntity user) {
    authAuthorityDao.remove(user);
    authUserDao.remove(user);
  }
}
