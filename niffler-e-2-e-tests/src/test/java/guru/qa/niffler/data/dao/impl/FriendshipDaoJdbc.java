package guru.qa.niffler.data.dao.impl;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;
import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class FriendshipDaoJdbc implements FriendshipDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {

    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)"
    )) {
      ps.setObject(1, requester.getId());
      ps.setObject(2, addressee.getId());
      ps.setString(3, PENDING.name());
      ps.setDate(4, new java.sql.Date(new Date().getTime()));

      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
    addIncomeInvitation(addressee, requester);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    try (PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)")
    ) {
      friendshipPs.setObject(1, requester.getId());
      friendshipPs.setObject(2, addressee.getId());
      friendshipPs.setObject(3, ACCEPTED.name());
      friendshipPs.setObject(4, new java.sql.Date(new Date().getTime()));
      friendshipPs.execute();

      friendshipPs.setObject(1, addressee.getId());
      friendshipPs.setObject(2, requester.getId());
      friendshipPs.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
