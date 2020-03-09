import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Note } from './note';
import { NoteService } from './note.service';

describe('Note service: ', () => {

  const testNotes: Note[] = [
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
      status: 'deleted'
    },
    {
      _id: 'third_id',
      ownerID: 'test-id',
      body: 'Third test id body.',
      addDate: new Date(),
      expireDate: new Date(),
      status: 'template'
    }
  ];
  let noteService: NoteService;

  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    // Set up the mock handling of the HTTP requests
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    // Construct an instance of the service with the mock
    // HTTP client.
    noteService = new NoteService(httpClient);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('getNotesByOwner() calls api/notes',() => {
    noteService.getNotesByOwner('test-id').subscribe(
      notes => expect(notes).toBe(testNotes)
    );


    const req = httpTestingController.expectOne(
      (request) => request.url.startsWith(noteService.noteUrl) && request.params.has('ownerid')
    );

    expect(req.request.method).toEqual('GET');

   // Check that the name parameter was 'Chris'
    expect(req.request.params.get('ownerid')).toEqual('test-id');

    req.flush(testNotes);
  } );

  it('getNotesByOwnerAndStatus calls api/notes', () => {
    noteService.getNotesByOwner('test-id',{ status: 'active' }).subscribe(
      notes => expect(notes).toBe(testNotes)
    );

    const req = httpTestingController.expectOne(
      (request) => request.url.startsWith(noteService.noteUrl) && request.params.has('ownerid')
       && request.params.has('status')
    );

    expect(req.request.method).toEqual('GET');

    expect(req.request.params.get('ownerid')).toEqual('test-id');
    expect(req.request.params.get('status')).toEqual('active');

    req.flush(testNotes);
  });
});
