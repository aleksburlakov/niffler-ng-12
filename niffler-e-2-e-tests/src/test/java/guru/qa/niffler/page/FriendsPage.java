package guru.qa.niffler.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import java.util.List;

public class FriendsPage extends BasePage {

  private final ElementsCollection friendsTableRows = $$x("//h2[text()='My friends']/following-sibling::table[1]//tr");
  private final ElementsCollection incomeRequestsTableRows =
      $$x("//h2[text()='Friend requests']/following-sibling::table[1]//tr");
  private final ElementsCollection allPeopleTableRows = $$("#all tr");
  private final SelenideElement noFriendsText = $x("//p[text()='There are no users yet']");
  private final SelenideElement allPeopleTab = $("a[href='/people/all']");

  public FriendsPage checkFriendsTableIsEmpty() {
    friendsTableRows.shouldBe(size(0));
    noFriendsText.shouldBe(visible);
    return this;
  }

  public FriendsPage checkFriendsInFriendsTable(List<String> friends) {
    friendsTableRows.shouldHave(size(friends.size()));
    friends.forEach(friend ->
    {
      var friendTableRow = friendsTableRows.findBy(text(friend));
      friendTableRow.shouldBe(visible);
      friendTableRow.$$("button").findBy(text("Unfriend")).shouldBe(visible, enabled);
    });
    return this;
  }

  public FriendsPage checkIncomeInvitationBePresentInFriendsTable(List<String> incomeFriends) {
    incomeRequestsTableRows.shouldHave(size(incomeFriends.size()));
    incomeFriends.forEach(incomeFriend ->
    {
      var incomeRequestTableRow = incomeRequestsTableRows.findBy(text(incomeFriend));
      incomeRequestTableRow.shouldBe(visible);
      incomeRequestTableRow.$$("button").findBy(text("Accept")).shouldBe(visible, enabled);
      incomeRequestTableRow.$$("button").findBy(text("Decline")).shouldBe(visible, enabled);
    });
    return this;
  }

  public FriendsPage navigateToAllPeopleTab() {
    allPeopleTab.shouldBe(visible).click();
    return this;
  }

  public FriendsPage checkOutcomeInvitationBePresent(List<String> outcomeFriends) {
    allPeopleTableRows.shouldHave(sizeGreaterThan(0));
    outcomeFriends.forEach(
        outcomeFriend -> {
          var outcomeFriendRow = allPeopleTableRows.findBy(text(outcomeFriend));
          outcomeFriendRow.shouldBe(visible);
          outcomeFriendRow.$$("span")
              .findBy(text("Waiting..."))
              .shouldBe(visible);
        });
    return this;
  }
}
