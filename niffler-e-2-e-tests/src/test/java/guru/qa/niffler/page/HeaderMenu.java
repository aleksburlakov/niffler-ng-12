package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

public class HeaderMenu {

  private final SelenideElement profileMenuButton = $("button[aria-label='Menu']");
  private final SelenideElement profileMenu = $("ul[role='menu']");

  public HeaderMenu clickProfileButton() {
    profileMenuButton.shouldBe(visible).shouldBe(enabled);
    profileMenuButton.click();
    profileMenu.shouldBe(visible);
    return this;
  }

  public FriendsPage chooseFriendsItem() {
    clickProfileMenuItem("Friends");
    return new FriendsPage();
  }

  private void clickProfileMenuItem(String profileMenuItem) {
    profileMenu
        .$x("./li/a[text()='" + profileMenuItem + "']")
        .shouldBe(visible)
        .click();
  }
}
