package guru.qa.niffler.test.fake;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class JdbcTest {

  static UsersDbClient usersDbClient = new UsersDbClient();
  static SpendDbClient spendDbClient = new SpendDbClient();

  @ValueSource(strings = {
      "valentin-105"
  })
  @ParameterizedTest
  void usersDbClientTest(String username) {
    usersDbClient.createUser(username, "12345");
    UserJson user = usersDbClient.findUserByUsername(username).get();
    Assertions.assertEquals(username, user.username());

    UUID id = user.id();
    user = usersDbClient.findUserById(id).get();
    Assertions.assertEquals(username, user.username());

    String updatedUserFirstname = "valentin-1800-firstname";
    UserEntity userEntity = UserEntity.fromJson(user);
    userEntity.setFirstname(updatedUserFirstname);
    usersDbClient.updateUser(UserJson.fromEntity(userEntity));
    user = usersDbClient.findUserById(id).get();
    Assertions.assertEquals(updatedUserFirstname, user.firstname());

    usersDbClient.addOutcomeInvitation(user, 1);
    usersDbClient.addIncomeInvitation(user, 1);
    usersDbClient.addFriend(user, 1);

    usersDbClient.deleteUser(user);
    Assertions.assertTrue(usersDbClient.findUserById(id).isEmpty());
  }

  @CsvSource({
      "education3, valentin-1300"
  })
  @ParameterizedTest
  void spendTest(String categoryName, String username) {
    CategoryJson categoryJson = new CategoryJson(null, categoryName, username, false);
    spendDbClient.createCategory(categoryJson);

    Optional<CategoryJson> optionalCategory = spendDbClient.findCategoryByUsernameAndSpendName(username, categoryName);
    Assertions.assertTrue(optionalCategory.isPresent());

    categoryJson = optionalCategory.get();
    UUID categoryId = categoryJson.id();
    optionalCategory = spendDbClient.findCategoryById(categoryId);
    Assertions.assertTrue(optionalCategory.isPresent());
    categoryJson = optionalCategory.get();
    Assertions.assertEquals(categoryName, categoryJson.name());

    CategoryJson updatedCategoryJson = new CategoryJson(
        categoryId,
        categoryName,
        username,
        true);

    spendDbClient.updateCategory(updatedCategoryJson);
    categoryJson = spendDbClient.findCategoryById(categoryId).get();
    Assertions.assertEquals(true, categoryJson.archived());


    SpendJson spendJson = new SpendJson(
        null,
        new Date(),
        categoryJson,
        CurrencyValues.EUR,
        12.00,
        "Тест",
        username);
    spendDbClient.createSpend(spendJson);

    Optional<SpendJson> optionalSpend = spendDbClient.findSpendByUsernameAndDescription(spendJson.username(), spendJson.description());
    Assertions.assertTrue(optionalSpend.isPresent());
    UUID spendId = optionalSpend.get().id();

    optionalSpend = spendDbClient.findSpendById(spendId);
    Assertions.assertTrue(optionalSpend.isPresent());

    double updatedAmount = 99.00;
    SpendJson updatedSpendJson = new SpendJson(
        spendId,
        spendJson.spendDate(),
        spendJson.category(),
        spendJson.currency(),
        updatedAmount,
        spendJson.description(),
        spendJson.username()
    );
    spendDbClient.updateSpend(updatedSpendJson);
    spendJson = spendDbClient.findSpendById(spendId).get();
    Assertions.assertEquals(updatedAmount, spendJson.amount());

    spendDbClient.deleteSpend(spendJson);
    Assertions.assertTrue(spendDbClient.findSpendById(spendId).isEmpty());

    spendDbClient.deleteCategory(categoryJson);
    Assertions.assertTrue(spendDbClient.findCategoryById(categoryId).isEmpty());
  }
}
