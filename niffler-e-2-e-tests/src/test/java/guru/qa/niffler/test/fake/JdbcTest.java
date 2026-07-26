package guru.qa.niffler.test.fake;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import java.util.Date;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
  void xaTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUser(
        new UserJson(
            null,
            "valentin-10",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  @Test
  void springJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserSpringJdbc(
        new UserJson(
            null,
            "valentin-11",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  @Test
  void jdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserJdbc(
        new UserJson(
            null,
            "valentin-6",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  @Test
  void jdbcXaTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserSpringJdbcXaTx(
        new UserJson(
            null,
            "valentin-7",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  @Test
  void chainedTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserSpringJdbcChainedTx(
        new UserJson(
            null,
            "valentin-8",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }
}
