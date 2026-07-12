package guru.qa.niffler.service;

import static guru.qa.niffler.data.Databases.transaction;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import java.sql.Connection;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();
  private static final int ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

  @Override
  public SpendJson createSpend(SpendJson spend) {
    return transaction(
        ISOLATION_LEVEL,
        connection -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                .create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
          }
          return SpendJson.fromEntity(
              new SpendDaoJdbc(connection).create(spendEntity)
          );
        },
        CFG.spendJdbcUrl()
    );
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    return transaction(
        ISOLATION_LEVEL,
        connection -> {
          return CategoryJson.fromEntity(
              new CategoryDaoJdbc(connection).create(
                  CategoryEntity.fromJson(category)
              )
          );
        },
        CFG.spendJdbcUrl()
    );
  }

  @Override
  public CategoryJson updateCategory(CategoryJson category) {
    return transaction(
        ISOLATION_LEVEL,
        connection -> {
          return CategoryJson.fromEntity(
              new CategoryDaoJdbc(connection).update(
                  CategoryEntity.fromJson(category)
              )
          );
        },
        CFG.spendJdbcUrl()
    );
  }
}
