package guru.qa.niffler.test.fake;

import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.UserDbClient;
import java.util.Arrays;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;

@Disabled
public class JdbcTest {

  @Test
  void txTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    SpendJson spend = spendDbClient.createSpend(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "cat-name-tx-2",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-tx",
            null
        )
    );

    System.out.println(spend);
  }

  @Test
  void createUserWithAuthoritiesTest() {
    UserClient userClient = new UserDbClient();

    AuthUserJson authUserJson = new AuthUserJson(
        null,
        "alexey",
        "12345",
        true,
        true,
        true,
        true
    );

    AuthUserJson user = userClient.createAuthUser(
        authUserJson,
        Arrays.asList(
            new AuthAuthorityJson(null, AuthAuthorityEntity.Authority.read, authUserJson),
            new AuthAuthorityJson(null, AuthAuthorityEntity.Authority.write, authUserJson)
        )
    );

    System.out.println("User " + user + " is created");
  }
}
