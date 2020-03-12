import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { Note, NoteStatus } from './note';

@Injectable()
export class NoteService {
  readonly noteUrl: string = environment.API_URL + 'notes';

  constructor(private httpClient: HttpClient) {}


  /**
   *
   * @param ownerID: _id of the owner
   * whose notes are being retrieved.
   *
   * @returns a list of the notes belonging to this owner, filtered by body and status
   *
   */
  getNotesByOwner(ownerID: string, filters?: { body?: string, status?: NoteStatus} ): Observable<Note[]> {
    let httpParams: HttpParams = new HttpParams();
    httpParams = httpParams.set('ownerid', ownerID);  // Ensure we are getting notes belonging to this owner
    if (filters) {
      if (filters.body) {
        httpParams = httpParams.set('body', filters.body);
      }
      if (filters.status) {
        httpParams = httpParams.set('status', filters.status);
      }
    }
    return this.httpClient.get<Note[]>(this.noteUrl, {
      params: httpParams,
    });
  }


  /**
   * @param id: _id of the note being retrieved
   * @param ownerID: _id of the owner who is requesting this note (fails if not a match in the note)
   *
   * @returns a single note with `id` belonging to `ownerID`
   */
  getNoteById(id: string, ownerID: string): Observable<Note> {
    let httpParams: HttpParams = new HttpParams();
    httpParams = httpParams.set('ownerid', ownerID); // Ensure we are getting a note that belongs to this owner
    return this.httpClient.get<Note>(this.noteUrl + '/' + id, {
      params: httpParams,
    });
  }

  /**
   *
   * @param notes: the list of notes being filtered
   * @param filters: filtering by `addDate` and `expireDate`
   */
  filterNotes(notes: Note[], filters: { addDate?: Date, expireDate?: Date } ): Note[] {

    let filteredNotes = notes;

   /* // Filter by addDate
    if (filters.addDate.toISOString()) {
      filteredNotes = filteredNotes.filter(note => {
        return note.addDate.indexOf(filters.addDate) !== -1;
      });
    }
    // Filter by expireDate
    if (filters.expireDate) {
      filteredNotes = filteredNotes.filter(note => {
        return note.expireDate.toISOString().indexOf(filters.expireDate.toISOString()) !== -1;
      });
    }
*/
    return filteredNotes;
  }

  addNewNote(newNote: Note): Observable<string> {
    // Send a post request to add a new note with the note data as the body.
    const test = this.httpClient.post<{id: string}>(this.noteUrl + '/new', newNote).pipe(map(res => res.id));
    return this.httpClient.post<{id: string}>(this.noteUrl + '/new', newNote).pipe(map(res => res.id));
  }

  // To implement
 // deleteNote()
  //editNote()
}
