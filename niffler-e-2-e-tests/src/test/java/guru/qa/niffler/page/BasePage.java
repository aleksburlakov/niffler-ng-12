package guru.qa.niffler.page;

public class BasePage {

  private final HeaderMenu headerMenu = new HeaderMenu();

  public HeaderMenu clickProfileButton() {
    return headerMenu.clickProfileButton();
  }
}
