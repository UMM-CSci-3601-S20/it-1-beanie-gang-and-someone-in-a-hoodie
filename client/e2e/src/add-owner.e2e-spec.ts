import {browser, protractor, by, element, utils} from 'protractor';
import { AddOwnerPage, TestOwner } from './add-owner.po';
import { E2EUtil } from './e2e.util';
import { emit } from 'cluster';

describe('Add owner', () => {
  let page: AddOwnerPage;
  const EC = protractor.ExpectedConditions;

  beforeEach(() => {
    page = new AddOwnerPage();
    page.navigateTo();
  });

  it('should have the correct title', () => {
    expect(page.getTitle()).toEqual('New Owner');
  });

  it('should enable and disable the add owner button', async () => {
    expect(element(by.buttonText('ADD OWNER')).isEnabled()).toBe(false);
    await page.typeInput('nameField', 'test');
    expect(element(by.buttonText('ADD OWNER')).isEnabled()).toBe(false);
    await page.typeInput('buildingField', 'test');
    expect(element(by.buttonText('ADD OWNER')).isEnabled()).toBe(false);
    await page.typeInput('officeNumberField', 'test');
    expect(element(by.buttonText('ADD OWNER')).isEnabled()).toBe(false);
    await page.typeInput('emailField', 'billydavis@gmail.com');
    expect(element(by.buttonText('ADD OWNER')).isEnabled()).toBe(false);
  });

  it('should add a new owner and go to the correct page', async () => {
    const owner: TestOwner = {
      name: E2EUtil.randomText(10),
      building: E2EUtil.randomText(15),
      officeNumber: E2EUtil.randomText(4),
      email: E2EUtil.randomText(5) + '@yahoo.com'
    };

    await page.addOwner(owner);

    // Wait until the URL does not contain 'owners/new'
    await browser.wait(EC.not(EC.urlContains('owners/new')), 10000);

    const url = await page.getUrl();
    expect(RegExp('.*\/owners\/[0-9a-fA-F]{24}$', 'i').test(url)).toBe(true);
    expect(url.endsWith('/owners/new')).toBe(false);

    expect(element(by.id('nameField')).getText()).toEqual(owner.name);
    expect(element(by.id('buildingField')).getText()).toEqual(owner.building);
    expect(element(by.id('officeNumberField')).getText()).toEqual(owner.officeNumber);
    expect(element(by.id('emailField')).getText()).toEqual(owner.email);
  });

});

