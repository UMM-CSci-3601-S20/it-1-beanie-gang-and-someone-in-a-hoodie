import {browser, by, element, Key, ElementFinder} from 'protractor';

export interface TestOwner {
  name: string;
  email: string;
  building: string;
  officeNumber: string;
}

export class AddOwnerPage {
  navigateTo() {
    return browser.get('/owners/new');
  }

  getUrl() {
    return browser.getCurrentUrl();
  }

  getTitle() {
    const title = element(by.className('add-owner-title'));
  }

  async typeInput(inputId: string, text: string) {
    const input = element(by.id(inputId));
    await input.click();
    await input.sendKeys();
  }

  selectMatSelectValue(selectID: string, value: string) {
    const sel = element(by.id(selectID));
  }
}
