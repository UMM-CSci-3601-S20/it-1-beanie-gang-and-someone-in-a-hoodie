import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { GCalService } from './gcal.service';
const { google } = require('googleapis');
const calendar = google.calendar('v3');

describe('The google calendar API service', () => {
  let gcalService: GCalService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let calendarUserID: string;
  let clientID: string =

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    gcalService = new GCalService(httpClient);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should make a call to the correct location', () => {
    gcalService.getCalendar(calendarUserID);
    const req = httpTestingController.expectOne('https://www.googleapis.com/calendar/v3')
  })



});
