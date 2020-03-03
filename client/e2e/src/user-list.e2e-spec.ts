import {UserPage} from './user-list.po';
import {browser, protractor, by, element} from 'protractor';

describe('User list', () => {
  let page: UserPage;
  const EC = protractor.ExpectedConditions;

  beforeEach(() => {
    page = new UserPage();
    page.navigateTo();
  });

  it('Should have the correct title', () => {
    expect(page.getUserTitle()).toEqual('Users');
  });

  it('Should type something in the name filter and check that it returned correct elements', async () => {
    await page.typeInput('user-name-input', 'Lynn Ferguson');

    // All of the user cards should have the name we are filtering by
    page.getUserCards().each(e => {
      expect(e.element(by.className('user-card-name')).getText()).toEqual('Lynn Ferguson');
    });
  });

  it('Should type something in the company filter and check that it returned correct elements', async () => {
    await page.typeInput('user-company-input', 'OHMNET');

    // All of the user cards should have the company we are filtering by
    page.getUserCards().each(e => {
      expect(e.element(by.className('user-card-company')).getText()).toEqual('OHMNET');
    });
  });

  it('Should type something partial in the company filter and check that it returned correct elements', async () => {
    await page.typeInput('user-company-input', 'ti');

    // Go through each of the cards that are being shown and get the companies
    const companies = await page.getUserCards().map(e => e.element(by.className('user-card-company')).getText());

    // We should see these companies
    expect(companies).toContain('MOMENTIA');
    expect(companies).toContain('KINETICUT');

    // We shouldn't see these companies
    expect(companies).not.toContain('DATAGENE');
    expect(companies).not.toContain('OHMNET');
  });

  it('Should type something in the age filter and check that it returned correct elements', async () => {
    await page.typeInput('user-age-input', '27');

    // Go through each of the cards that are being shown and get the names
    const names = await page.getUserCards().map(e => e.element(by.className('user-card-name')).getText());

    // We should see these users whose age is 27
    expect(names).toContain('Stokes Clayton');
    expect(names).toContain('Bolton Monroe');
    expect(names).toContain('Merrill Parker');

    // We shouldn't see these users
    expect(names).not.toContain('Connie Stewart');
    expect(names).not.toContain('Lynn Ferguson');
  });

  it('Should change the view', async () => {
    await page.changeView('list');

    expect(page.getUserCards().count()).toEqual(0); // There should be no cards
    expect(page.getUserListItems().count()).toBeGreaterThan(0); // There should be list items
  });

  it('Should select a role, switch the view, and check that it returned correct elements', async () => {
    await page.selectMatSelectValue('user-role-select', 'viewer');
    await page.changeView('list');

    expect(page.getUserListItems().count()).toBeGreaterThan(0);

    // All of the user list items should have the role we are looking for
    page.getUserListItems().each(e => {
      expect(e.element(by.className('user-list-role')).getText()).toEqual('viewer');
    });


  });

  it('Should click view profile on a user and go to the right URL', async () => {
    const firstUserName = await page.getUserCards().first().element(by.className('user-card-name')).getText();
    const firstUserCompany = await page.getUserCards().first().element(by.className('user-card-company')).getText();
    await page.clickViewProfile(page.getUserCards().first());

    // Wait until the URL contains 'users/' (note the ending slash)
    await browser.wait(EC.urlContains('users/'), 10000);

    // When the view profile button on the first user card is clicked, the URL should have a valid mongo ID
    const url = await page.getUrl();
    expect(RegExp('.*\/users\/[0-9a-fA-F]{24}$', 'i').test(url)).toBe(true);

    // On this profile page we were sent to, the name and company should be correct
    expect(element(by.className('user-card-name')).getText()).toEqual(firstUserName);
    expect(element(by.className('user-card-company')).getText()).toEqual(firstUserCompany);
  });

  it('Should click add user and go to the right URL', async () => {
    await page.clickAddUserFAB();

    // Wait until the URL contains 'users/new'
    await browser.wait(EC.urlContains('users/new'), 10000);

    // When the view profile button on the first user card is clicked, we should be sent to the right URL
    const url = await page.getUrl();
    expect(url.endsWith('/users/new')).toBe(true);

    // On this profile page we were sent to, We should see the right title
    expect(element(by.className('add-user-title')).getText()).toEqual('New User');
  });

});
