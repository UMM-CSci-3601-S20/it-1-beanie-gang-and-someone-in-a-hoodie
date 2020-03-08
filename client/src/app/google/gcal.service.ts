import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { HttpTestingController } from '@angular/common/http/testing';


@Injectable()
export class GCalService {
  constructor(private httpClient: HttpClient) {
  }

}
