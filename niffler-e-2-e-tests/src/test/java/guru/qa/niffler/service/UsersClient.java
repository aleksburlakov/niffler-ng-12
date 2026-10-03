package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.Optional;
import java.util.UUID;

public interface UsersClient {

  UserJson createUser(String username, String password);

  UserJson updateUser(UserJson user);

  Optional<UserJson> findUserById(UUID id);

  Optional<UserJson> findUserByUsername(String username);

  void addIncomeInvitation(UserJson targetUser, int count);

  void addOutcomeInvitation(UserJson targetUser, int count);

  void addFriend(UserJson targetUser, int count);

  void deleteUser(UserJson user);
}
