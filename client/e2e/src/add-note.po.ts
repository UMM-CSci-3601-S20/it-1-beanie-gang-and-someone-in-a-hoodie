import {browser, by, element, Key, ElementFinder} from 'protractor';
import {NoteStatus} from '../../src/app/notes/note';

export interface TestNote {
  body: string;
  status: NoteStatus;
}

export class AddNotePage {
  navigateTo() {
    return browser.get('/notes/new');
  }

  getUrl() {
    return browser.getCurrentUrl();
  }

  getTitle() {
    const title = element(by.className('add-note-title')).getText();
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

  clickAddNote() {
    return element(by.buttonText('ADD NOTE')).click();
  }

  async addNote(newNote: TestNote) {
    await this.typeInput('bodyField', newNote.body);
    await this.selectMatSelectValue('statusField', 'active');
    return this.clickAddNote();
  }

}
