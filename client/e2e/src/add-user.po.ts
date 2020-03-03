import {browser, by, element, Key, ElementFinder} from 'protractor';

export interface TestUser {
  name: string;
  age: string;
  company?: string;
  email?: string;
  role: 'admin' | 'editor' | 'viewer';
}

export class AddUserPage {
  navigateTo() {
    return browser.get('/users/new');
  }

  getUrl() {
    return browser.getCurrentUrl();
  }

  getTitle() {
    const title = element(by.className('add-user-title')).getText();
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

  clickAddUser() {
    return element(by.buttonText('ADD USER')).click();
  }

  async addUser(newUser: TestUser) {
    await this.typeInput('nameField', newUser.name);
    await this.typeInput('ageField', newUser.age);
    if (newUser.company) {
      await this.typeInput('companyField', newUser.company);
    }
    if (newUser.email) {
      await this.typeInput('emailField', newUser.email);
    }
    await this.selectMatSelectValue('roleField', newUser.role);
    return this.clickAddUser();
  }
}
