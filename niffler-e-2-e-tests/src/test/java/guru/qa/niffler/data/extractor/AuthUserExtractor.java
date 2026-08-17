package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class AuthUserExtractor implements ResultSetExtractor<AuthUserEntity> {

  public static final AuthUserExtractor instance = new AuthUserExtractor();

  private AuthUserExtractor() {
  }

  @Override
  public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
    Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
    UUID userId = null;
    while (rs.next()) {
      userId = rs.getObject("id", UUID.class);
      AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
        try {
          AuthUserEntity entity = new AuthUserEntity();
          entity.setId(rs.getObject("id", UUID.class));
          entity.setUsername(rs.getString("username"));
          entity.setPassword(rs.getString("password"));
          entity.setEnabled(rs.getBoolean("enabled"));
          entity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          entity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          entity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          return entity;
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      });

      AuthorityEntity authority = new AuthorityEntity();
      authority.setId(rs.getObject("authority_id", UUID.class));
      authority.setAuthority(Authority.valueOf(rs.getString("authority")));
      user.getAuthorities().add(authority);
    }
    return userMap.get(userId);
  }
}
