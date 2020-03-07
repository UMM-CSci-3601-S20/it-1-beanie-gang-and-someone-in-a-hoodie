import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Owner } from '../app/owner/owner';
import { OwnerService } from '../app/owner/owner.service';

/**
 * A mock version of OwnerService used for testing
 */
@Injectable()
export class MockOwnerService extends OwnerService {
  static testOwners: Owner[] = [
    {
      _id: 'chris_id',
      name: 'Chris',
      building: 'Science Hall',
      email: 'chris@this.that',
      officeNumber: '1001'
    },
    {
      _id: 'richard_id',
      name: 'Richard Mars',
      building: 'HFA',
      email: 'mars@this.that',
      officeNumber: '2022'
    },
    {
      _id: 'jamie_id',
      name: 'Jamie',
      building: 'Humanities',
      email: 'totallyrealemail@this.that',
      officeNumber: '111'
    }
  ];

  constructor() {
    super(null);
  }

  getOwners(filters?: { name?: string, email?: string, building?: string, officeNumber?: string }): Observable<Owner[]> {
    return of(MockOwnerService.testOwners);
  }

  getOwnerById(id: string): Observable<Owner> {

    if (id === MockOwnerService.testOwners[0]._id) {
      return of(MockOwnerService.testOwners[0]);
    } else {
      return of(null);
    }
  }

}
