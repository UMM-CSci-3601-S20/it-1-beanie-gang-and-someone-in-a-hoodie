import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Note } from '../app/notes/note';
import { NoteService } from '../app/notes/note.service';

@Injectable()
export class MockNoteService extends NoteService {
  static testActiveNotes: Note[] = [
    {
      _id: 'first_id',
      ownerID: 'test-id',
      body: 'This is the body of the first test id. It is somewhat long.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'active'
    },
    {
      _id: 'second_id',
      ownerID: 'test-id',
      body: 'This is the second test id.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'active'
    }
  ];
  static testTemplateNotes: Note[] = [
    {
      _id: 'third_id',
      ownerID: 'test-id',
      body: 'Third test id body.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'template'
    },
    {
      _id: 'fourth_id',
      ownerID: 'test-id',
      body: 'This is the fourth test id.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'template'
    }
  ];
  static testDraftNotes: Note[] = [
    {
      _id: 'fifth_id',
      ownerID: 'test-id',
      body: 'Fifth id test body.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'draft'
    },
    {
      _id: 'sixth_id',
      ownerID: 'test-id',
      body: 'Sixth id test body.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'draft'
    }
  ];
  static testDeletedNotes: Note[] = [
    {
      _id: 'seventh_id',
      ownerID: 'test-id',
      body: 'Fifth id test body.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'deleted'
    },
    {
      _id: 'eighth_id',
      ownerID: 'test-id',
      body: 'Eighth id test body.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'deleted'
    }
  ];


  constructor() {
    super(null);
  }

  getNotesByOwner(OwnerId: string): Observable<Map<string, Note[]>> {
    const outputMap = new Map<string, Note[]>();
    outputMap.set('active', MockNoteService.testActiveNotes);
    outputMap.set('template', MockNoteService.testTemplateNotes);
    outputMap.set('draft', MockNoteService.testDraftNotes);
    outputMap.set('deleted', MockNoteService.testDeletedNotes);
    return of(outputMap);
  }

}
