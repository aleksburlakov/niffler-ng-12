package guru.qa.niffler.data.dao.impl;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;
import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import org.springframework.jdbc.core.JdbcTemplate;

public class FriendshipDaoSpringJdbc implements FriendshipDao {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.userdataJdbcUrl();

  @Override
  public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
    jdbcTemplate.update(
        "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
            "VALUES (?, ?, ?, ?)",
        requester.getId(),
        addressee.getId(),
        FriendshipStatus.PENDING.name(),
        LocalDate.now()
    );
  }

  @Override
  public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
    addIncomeInvitation(addressee, requester);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
    String queryPattern = "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(
        queryPattern,
        requester.getId(),
        addressee.getId(),
        FriendshipStatus.ACCEPTED.name(),
        LocalDate.now()
    );

    jdbcTemplate.update(
        queryPattern,
        addressee.getId(),
        requester.getId(),
        FriendshipStatus.ACCEPTED.name(),
        LocalDate.now()
    );
  }
}
