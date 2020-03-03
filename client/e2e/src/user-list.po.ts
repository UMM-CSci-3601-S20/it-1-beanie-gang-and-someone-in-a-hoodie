import {browser, by, element, Key, ElementFinder} from 'protractor';

export class UserPage {
  navigateTo() {
    return browser.get('/users');
  }

  getUrl() {
    return browser.getCurrentUrl();
  }

  getUserTitle() {
    const title = element(by.className('user-list-title')).getText();
    return title;
  }

  async typeInput(inputId: string, text: string) {
    const input = element(by.id(inputId));
    await input.click();
    await input.sendKeys(text);
  }

  selectMatSelectValue(selectID: string, value: string) {
    const sel = element(by.id(selectID));
    return sel.click().then(() => {
      return element(by.css('mat-option[value="' + value + '"]')).click();
    });
  }

  getUserCards() {
    return element(by.className('user-cards-container')).all(by.tagName('app-user-card'));
  }

  getUserListItems() {
    return element(by.className('user-nav-list')).all(by.className('user-list-item'));
  }

  clickViewProfile(card: ElementFinder) {
    return card.element(by.buttonText('VIEW PROFILE')).click();
  }

  changeView(viewType: 'card' | 'list') {
    return element(by.id('view-type-radio')).element(by.css('mat-radio-button[value="' + viewType + '"]')).click();
  }

  clickAddUserFAB() {
    return element(by.className('add-user-fab')).click();
  }
}
