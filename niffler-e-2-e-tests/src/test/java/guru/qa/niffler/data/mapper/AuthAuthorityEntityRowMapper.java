package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class AuthAuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

  public static final AuthAuthorityEntityRowMapper instance = new AuthAuthorityEntityRowMapper();

  private AuthAuthorityEntityRowMapper() {

  }

  @Override
  public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    AuthorityEntity authority = new AuthorityEntity();
    authority.setId(rs.getObject("id", UUID.class));
    AuthUserEntity user = new AuthUserEntity();
    user.setId(rs.getObject("user_id", UUID.class));
    authority.setUserId(user.getId());
    authority.setAuthority(Authority.valueOf(rs.getString("authority")));
    return authority;
  }
}
