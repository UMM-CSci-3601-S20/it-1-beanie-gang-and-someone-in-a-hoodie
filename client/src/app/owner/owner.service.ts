import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Owner } from './owner';


export class OwnerService {

  constructor(httpClient: HttpClient) {}

  getOwnerById(id: string): Owner {
    return null;
  }
}
