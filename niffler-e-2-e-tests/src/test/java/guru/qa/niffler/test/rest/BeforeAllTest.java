package guru.qa.niffler.test.rest;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

@Order(0)
public class BeforeAllTest {

  private final UsersClient usersClient = UsersClient.getInstance();

  @Test
  void checkUsersDbIsEmptyTest() {
    List<UserJson> users = usersClient.findAll("duck", null);
    Assertions.assertNotNull(users);
    Assertions.assertTrue(users.isEmpty());
  }
}
