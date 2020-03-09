import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Note } from '../app/notes/note';
import { NoteService } from '../app/notes/note.service';

@Injectable()
export class MockNoteService extends NoteService {
  static testNotes: Note[] = [
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
    },
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
    },
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
    },
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

  getNotesByOwner(OwnerId: string): Observable<Note[]> {
   let notesObtained: Note[];
   let amount = 0;
   for(let i = 0; i < 8; i++){
      if (OwnerId === MockNoteService.testNotes[i].ownerID) {
        notesObtained[amount] = MockNoteService.testNotes[i];
        amount++;
      }
    }
   return of(notesObtained);

  }

}
