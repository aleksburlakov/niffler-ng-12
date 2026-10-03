package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.authJdbcUrl();

  @Override
  public void create(AuthorityEntity... authority) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
        "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS)) {
      for (AuthorityEntity a : authority) {
        ps.setObject(1, a.getUser().getId());
        ps.setString(2, a.getAuthority().name());
        ps.addBatch();
        ps.clearParameters();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<AuthorityEntity> findByUserId(UUID userId) {
    List<AuthorityEntity> userAuthorities = new ArrayList<>();
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM \"authority\" WHERE user_id = ?"
    )) {
      ps.setObject(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          AuthorityEntity authority = new AuthorityEntity();
          authority.setId(rs.getObject("id", UUID.class));
          authority.setAuthority(Authority.valueOf(rs.getString("authority")));
          userAuthorities.add(authority);
        }
      }
      return userAuthorities;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<AuthorityEntity> findAll() {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
        "SELECT * FROM \"authority\"")) {
      ps.execute();
      List<AuthorityEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setId(rs.getObject("id", UUID.class));
//          ae.setUserId(rs.getObject("user_id", UUID.class));
          ae.setAuthority(Authority.valueOf(rs.getString("authority")));
          result.add(ae);
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(AuthUserEntity user) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
        "DELETE FROM \"authority\" WHERE user_id = ?")) {
      ps.setObject(1, user.getId());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
