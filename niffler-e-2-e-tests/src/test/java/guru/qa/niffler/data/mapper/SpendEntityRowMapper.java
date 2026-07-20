package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class SpendEntityRowMapper implements RowMapper<SpendEntity> {

  public static SpendEntityRowMapper instance = new SpendEntityRowMapper();

  private SpendEntityRowMapper() {

  }

  @Override
  public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    SpendEntity entity = new SpendEntity();
    entity.setId(rs.getObject("spend_id", UUID.class));
    entity.setUsername(rs.getString("spend_username"));
    entity.setCurrency(CurrencyValues.valueOf(rs.getString("spend_currency")));
    entity.setSpendDate(Date.valueOf(rs.getObject("spend_date", LocalDate.class)));
    entity.setAmount(rs.getDouble("spend_amount"));
    entity.setDescription(rs.getString("spend_description"));
    entity.setCategory(CategoryEntityRowMapper.instance.mapRow(rs, rowNum));
    return entity;
  }
}