import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Owner } from '../app/owner/owner';
import { OwnerService } from '../app/owner/owner.service';


@Injectable()
export class MockOwnerService extends OwnerService {

  constructor() {
    super(null);
  }

  getOwnerById(id: string): Owner {
    let dummyOwner: Owner = {
      _id: 'test-id',
      name: 'Test Owner',
      email: 'test000@morris.umn.edu',
      building: 'Test Building',
      officeNumber: '000'
    };
    return dummyOwner;
  }
}
