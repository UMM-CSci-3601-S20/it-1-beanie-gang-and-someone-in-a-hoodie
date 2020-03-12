import {OwnerPage} from './owner-list.po';
import {browser, protractor, by, element} from 'protractor';

describe('Owner list', () => {
  let page: OwnerPage;
  const EC = protractor.ExpectedConditions;

  beforeEach(() => {
    page = new OwnerPage();
    page.navigateTo();
  });

  it('Should have the correct title', () => {
    expect(page.getOwnerTitle()).toEqual('Owners');
  });

  it('Should type something in the name filter and check that it returned correct elements', async () => {
    await page.typeInput('owner-name-input', 'Prospero Bruce');
    page.getOwnerListItems().each(e => {
      expect(e.element(by.className('owner-list-name')).getText()).toEqual('Prospero Bruce');
    });
  });

  it('Should type something in the building filter and check that it returned correct elements', async () => {
    await page.typeInput('owner-building-input', 'White House');
    page.getOwnerListItems().each(e => {
      expect(e.element(by.className('owner-list-building')).getText()).toEqual('White House');
    });
  });

  it('Should type something in the office number filter and check that it returned correct elements', async () => {
    await page.typeInput('owner-officenumber-input', '112');
    page.getOwnerListItems().each(e => {
      expect(e.element(by.className('owner-list-officenumber')).getText()).toEqual('112');
    });
  });


  it('Should type something in the office number filter and building filter and check that it returned correct elements', async () => {
    await page.typeInput('owner-officenumber-input', '112');
    await page.typeInput('owner-building-input', 'White House');

    page.getOwnerListItems().each(e => {
      expect(e.element(by.className('owner-list-officenumber')).getText()).toEqual('112');
      expect(e.element(by.className('owner-list-building')).getText()).toEqual('White House');
    });
  });

  it('Should type something partial in the company filter and check that it returned correct elements', async () => {
    await page.typeInput('owner-building-input', 'House');

    // Go through each of the cards that are being shown and get the companies
    const buildings = await page.getOwnerListItems().map(e => e.element(by.className('owner-list-building')).getText());

    // We should see these companies
    expect(buildings).toContain('White House');
    expect(buildings).toContain('Dancing House');
  });


  it('Should click on a owner and go to the correct URL', async () => {
    const ownerOneName = await page.getOwnerListItems().map(e => e.element(by.className('owner-list-name')).getText());
    const ownerOneBuilding = await page.getOwnerListItems().map(e => e.element(by.className('owner-list-building')).getText());

    await page.clickViewOwner();

    // Wait until the URL contains 'users/' (note the ending slash)
    await browser.wait(EC.urlContains('owners/'), 10000);

    // When the view profile button on the first user card is clicked, the URL should have a valid mongo ID
    const url = await page.getUrl();
    expect(RegExp('.*\/owners\/[0-9a-fA-F]{24}$', 'i').test(url)).toBe(true);
  });

  it('Should click add owner and go to the right URL', async () => {
    await page.clickAddOwnerFAB();

    // Wait until the URL contains 'users/new'
    await browser.wait(EC.urlContains('owners/new'), 10000);

    // When the view profile button on the first user card is clicked, we should be sent to the right URL
    const url = await page.getUrl();
    expect(url.endsWith('/owners/new')).toBe(true);

    // On this profile page we were sent to, We should see the right title
    expect(element(by.className('add-owner-title')).getText()).toEqual('New Owner');
  });

});
