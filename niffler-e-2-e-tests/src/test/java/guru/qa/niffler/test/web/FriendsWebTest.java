package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class FriendsWebTest {

  private static final Config CFG = Config.getInstance();

  @Test
  @ExtendWith(UsersQueueExtension.class)
  void friendShouldBePresentInFriendsTable(
      @UsersQueueExtension.UserType(UsersQueueExtension.UserType.Type.WITH_FRIEND)
      UsersQueueExtension.StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.password())
        .clickProfileButton()
        .chooseFriendsItem()
        .checkFriendsInFriendsTable(List.of(user.friend()));
  }

  @Test
  @ExtendWith(UsersQueueExtension.class)
  void friendsTableShouldBeEmptyForNewUser(
      @UsersQueueExtension.UserType(UsersQueueExtension.UserType.Type.EMPTY) UsersQueueExtension.StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.password())
        .clickProfileButton()
        .chooseFriendsItem()
        .checkFriendsTableIsEmpty();
  }

  @Test
  @ExtendWith(UsersQueueExtension.class)
  void incomeInvitationBePresentInFriendsTable(
      @UsersQueueExtension.UserType(UsersQueueExtension.UserType.Type.WITH_INCOME_REQUEST)
      UsersQueueExtension.StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.password())
        .clickProfileButton()
        .chooseFriendsItem()
        .checkIncomeInvitationBePresentInFriendsTable(List.of(user.incomeFriend()));
  }

  @Test
  @ExtendWith(UsersQueueExtension.class)
  void outcomeInvitationBePresentInAllPeoplesTable(
      @UsersQueueExtension.UserType(UsersQueueExtension.UserType.Type.WITH_OUTCOME_REQUEST)
      UsersQueueExtension.StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.password())
        .clickProfileButton()
        .chooseFriendsItem()
        .navigateToAllPeopleTab()
        .checkOutcomeInvitationBePresent(List.of(user.outcomeFriend()));
  }
}
