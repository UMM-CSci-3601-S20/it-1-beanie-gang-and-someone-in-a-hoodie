import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Owner } from './owner';
import { map } from 'rxjs/operators';

@Injectable()
export class OwnerService {
  readonly ownerUrl: string = environment.API_URL + 'owners';

  constructor(private httpClient: HttpClient) {
  }

  getOwnerById(id: string): Observable<Owner> {
    return this.httpClient.get<Owner>(this.ownerUrl + '/' + id);
  }

  filterOwners(owners: Owner[], filters: { name?: string, email?: string, building?: string, officeNumber?: string }): Owner[] {

    let filteredOwners = owners;

    // Filter by name
    if (filters.name) {
      filters.name = filters.name.toLowerCase();

      filteredOwners = filteredOwners.filter(owner => {
        return owner.name.toLowerCase().indexOf(filters.name) !== -1;
      });
    }

    // Filter by email
    if (filters.email) {
      filters.email = filters.email.toLowerCase();

      filteredOwners = filteredOwners.filter(owner => {
        return owner.email.toLowerCase().indexOf(filters.email) !== -1;
      });
    }

    // Filter by building
    if (filters.building) {
      filters.building = filters.building.toLowerCase();

      filteredOwners = filteredOwners.filter(owner => {
        return owner.building.toLowerCase().indexOf(filters.building) !== -1;
      });
    }

    // Filter by officeNumber
    if (filters.officeNumber) {
      filters.officeNumber = filters.officeNumber.toLowerCase();

      filteredOwners = filteredOwners.filter(owner => {
        return owner.officeNumber.toLowerCase().indexOf(filters.officeNumber) !== -1;
      });
    }

    return filteredOwners;
  }

  addOwner(newOwner: Owner): Observable<string> {
    // Send post request to add a new owner with the owner data as the body.
    return this.httpClient.post<{id: string}>(this.ownerUrl + '/new', newOwner).pipe(map(res => res.id));
  }
}
