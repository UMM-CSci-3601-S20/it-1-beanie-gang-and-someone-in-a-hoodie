import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Owner } from './owner';
import { OwnerService } from './owner.service';

describe('Owner service: ', () => {
  // A small collection of test owners
  const testOwners: Owner[] = [
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
      _id: 'william_id',
      name: 'William',
      building: 'Humanities',
      email: 'enterprise@this.that',
      officeNumber: '111'
    }
  ];
  let ownerService: OwnerService;
  // These are used to mock the HTTP requests so that we (a) don't have to
  // have the server running and (b) we can check exactly which HTTP
  // requests were made to ensure that we're making the correct requests.
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
    ownerService = new OwnerService(httpClient);
  });

  afterEach(() => {
    // After every test, assert that there are no more pending requests.
    httpTestingController.verify();
  });

  it('getOwners() calls api/owners', () => {

    ownerService.getOwners().subscribe(
      owners => expect(owners).toBe(testOwners)
    );
    const req = httpTestingController.expectOne(ownerService.ownerUrl);

    expect(req.request.method).toEqual('GET');

    req.flush(testOwners);
  });

  it('getOwners() calls api/owners with filter parameter \'name\'', () => {

    ownerService.getOwners({ name: 'Chris' }).subscribe(
      owners => expect(owners).toBe(testOwners)
    );

    // Specify that (exactly) one request will be made to the specified URL with the name parameter.
    const req = httpTestingController.expectOne(
      (request) => request.url.startsWith(ownerService.ownerUrl) && request.params.has('name')
    );

    // Check that the request made to that URL was a GET request.
    expect(req.request.method).toEqual('GET');

    // Check that the name parameter was 'Chris'
    expect(req.request.params.get('name')).toEqual('Chris');

    req.flush(testOwners);
  });

  it('getOwners() calls api/owners with filter parameter \'email\'', () => {

    ownerService.getOwners({ email: 'mars@this.that' }).subscribe(
      owners => expect(owners).toBe(testOwners)
    );

    // Specify that (exactly) one request will be made to the specified URL with the parameter.
    const req = httpTestingController.expectOne(
      (request) => request.url.startsWith(ownerService.ownerUrl) && request.params.has('email')
    );

    // Check that the request made to that URL was a GET request.
    expect(req.request.method).toEqual('GET');

    // Check that the parameter was correct
    expect(req.request.params.get('email')).toEqual('mars@this.that');

    req.flush(testOwners);
  });

  it('getOwners() calls api/owners with multiple filter parameters', () => {

    ownerService.getOwners({ name: 'william', building: 'Humanities', officeNumber: '111' }).subscribe(
      owners => expect(owners).toBe(testOwners)
    );

    // Specify that (exactly) one request will be made to the specified URL with the parameters.
    const req = httpTestingController.expectOne(
      (request) => request.url.startsWith(ownerService.ownerUrl)
        && request.params.has('name') && request.params.has('building') && request.params.has('officenumber')
    );

    // Check that the request made to that URL was a GET request.
    expect(req.request.method).toEqual('GET');

    // Check that the parameters are correct
    expect(req.request.params.get('name')).toEqual('william');
    expect(req.request.params.get('building')).toEqual('Humanities');
    expect(req.request.params.get('officenumber')).toEqual('111');

    req.flush(testOwners);
  });

  it('getOwnerById() calls api/owners/id', () => {
    const targetOwner: Owner = testOwners[1];
    const targetId: string = targetOwner._id;
    ownerService.getOwnerById(targetId).subscribe(
      owner => expect(owner).toBe(targetOwner)
    );

    const expectedUrl: string = ownerService.ownerUrl + '/' + targetId;
    const req = httpTestingController.expectOne(expectedUrl);
    expect(req.request.method).toEqual('GET');
    req.flush(targetOwner);
  });


  it('filterOwners() filters by building', () => {
    expect(testOwners.length).toBe(3);
    const ownerCompany = 'HFA';
    expect(ownerService.filterOwners(testOwners, { building: ownerCompany }).length).toBe(1);
  });

  it('addOwner() calls api/owners/new', () => {

    ownerService.addOwner(testOwners[1]).subscribe(
      id => expect(id).toBe('testid')
    );

    const req = httpTestingController.expectOne(ownerService.ownerUrl + '/new');

    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(testOwners[1]);

    req.flush({id: 'testid'});
  });
});
