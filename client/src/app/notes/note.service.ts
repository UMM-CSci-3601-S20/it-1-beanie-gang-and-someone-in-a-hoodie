import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { Note } from './note';

@Injectable()
export class NoteService {
  readonly noteUrl: string = environment.API_URL + 'notes';

  constructor(private httpClient: HttpClient) {}


  /**
   *
   * @param ownerId: identifier number of the owner
   * whose notes are being retrieved.
   *
   * @returns a map of the notes belonging to this owner,
   * with the keys 'active', 'template', 'draft', and 'deleted'.
   *
   */

  getNotesByOwner(ownerId: string): Observable<Map<string, Note[]>> {
    let httpParams: HttpParams = new HttpParams();
    httpParams = httpParams.set('id', ownerId);
    return this.httpClient.get<Map<string, Note[]>>(this.noteUrl, {
      params: httpParams,
    });
  }

}
