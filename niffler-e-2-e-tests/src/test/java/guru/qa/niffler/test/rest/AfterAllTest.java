package guru.qa.niffler.test.rest;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

@Isolated
public class AfterAllTest {

  private final UsersClient usersClient = UsersClient.getInstance();

  @Test
  void checkUsersDbIsNotEmptyTest() {
    List<UserJson> users = usersClient.findAll("duck", null);
    Assertions.assertNotNull(users);
    Assertions.assertFalse(users.isEmpty());
  }
}
