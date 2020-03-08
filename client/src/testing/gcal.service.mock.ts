import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { GCalService } from '../app/google/gcal.service';

@Injectable()
export class MockGCalService extends GCalService {

}
