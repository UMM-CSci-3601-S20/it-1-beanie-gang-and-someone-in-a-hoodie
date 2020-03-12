import {browser, protractor, by, element, utils} from 'protractor';
import { AddNotePage, TestNote } from './add-note.po';
import { E2EUtil } from './e2e.util';
import { emit } from 'cluster';

describe('Add note', () => {
  let page: AddNotePage;
  const EC = protractor.ExpectedConditions;

  beforeEach(() => {
    page = new AddNotePage();
    page.navigateTo();
  });

  it('should have the correct title', () => {
    expect(page.getTitle()).toEqual('New Note');
  });

  it('should enable and disable the add note button', async () => {
    expect(element(by.buttonText('ADD NOTE')).isEnabled()).toBe(false);
    await page.selectMatSelectValue('statusField', 'active');
    expect(element(by.buttonText('ADD NOTE')).isEnabled()).toBe(false);
    await page.typeInput('bodyField', 'test');
    expect(element(by.buttonText('ADD NOTE')).isEnabled()).toBe(true);
  });

  it('should add a new note and go to the correct page', async () => {
    const note: TestNote = {
      body: 'I will be in the lab until 3pm.',
      status: 'active',
    };

    await page.addNote(note);

    // Wait until the URL does not contain 'notes/new'
    await browser.wait(EC.not(EC.urlContains('notes/new')), 10000);

    const url = await page.getUrl();
    expect(RegExp('.*\/notes\/[0-9a-fA-F]{24}$', 'i').test(url)).toBe(true);
    expect(url.endsWith('/notes/new')).toBe(false);

    expect(element(by.id('statusField')).getText()).toEqual('active');
    expect(element(by.id('bodyField')).getText()).toEqual('I will be in the lab until 3pm.');
  });
});
