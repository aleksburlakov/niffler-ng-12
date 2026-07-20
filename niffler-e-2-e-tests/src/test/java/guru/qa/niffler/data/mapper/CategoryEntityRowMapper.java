package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {
  public static CategoryEntityRowMapper instance = new CategoryEntityRowMapper();

  private CategoryEntityRowMapper() {

  }

  @Override
  public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    CategoryEntity entity = new CategoryEntity();
    entity.setId(rs.getObject("category_id", UUID.class));
    entity.setName(rs.getString("category_name"));
    entity.setUsername(rs.getString("category_username"));
    entity.setArchived(rs.getBoolean("category_archived"));
    return entity;
  }
}
