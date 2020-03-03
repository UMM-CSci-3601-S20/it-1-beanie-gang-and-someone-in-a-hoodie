import {browser, protractor, by, element, utils} from 'protractor';
import { AddUserPage, TestUser } from './add-user.po';
import { E2EUtil } from './e2e.util';

describe('Add user', () => {
  let page: AddUserPage;
  const EC = protractor.ExpectedConditions;

  beforeEach(() => {
    page = new AddUserPage();
    page.navigateTo();
  });

  it('Should have the correct title', () => {
    expect(page.getTitle()).toEqual('New User');
  });


  it('Should enable and disable the add user button', async () => {
    expect(element(by.buttonText('ADD USER')).isEnabled()).toBe(false);
    await page.typeInput('nameField', 'test');
    expect(element(by.buttonText('ADD USER')).isEnabled()).toBe(false);
    await page.typeInput('ageField', '20');
    expect(element(by.buttonText('ADD USER')).isEnabled()).toBe(false);
    await page.typeInput('emailField', 'invalid');
    expect(element(by.buttonText('ADD USER')).isEnabled()).toBe(false);
    await page.typeInput('emailField', '@example.com');
    expect(element(by.buttonText('ADD USER')).isEnabled()).toBe(true);
  });

  it('Should add a new user and go to the right page', async () => {
    const user: TestUser = {
      name: E2EUtil.randomText(10),
      age: '30',
      company: E2EUtil.randomText(10),
      email: E2EUtil.randomText(5) + '@example.com',
      role: 'editor'
    };

    await page.addUser(user);

    // Wait until the URL does not contain 'users/new'
    await browser.wait(EC.not(EC.urlContains('users/new')), 10000);

    const url = await page.getUrl();
    expect(RegExp('.*\/users\/[0-9a-fA-F]{24}$', 'i').test(url)).toBe(true);
    expect(url.endsWith('/users/new')).toBe(false);

    expect(element(by.className('user-card-name')).getText()).toEqual(user.name);
    expect(element(by.className('user-card-company')).getText()).toEqual(user.company);
    expect(element(by.className('user-card-role')).getText()).toEqual(user.role);
    expect(element(by.className('user-card-age')).getText()).toEqual(user.age);
    expect(element(by.className('user-card-email')).getText()).toEqual(user.email);
  });

});
