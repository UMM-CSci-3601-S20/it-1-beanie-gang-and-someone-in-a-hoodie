import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Owner } from './owner';
import { OwnerService } from './owner.service';

describe('Owner service: ', () => {
  // A small collection of test owners NEED TO MAKE A LOT OF CHANGES HERE
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

  /*it('getOwners() calls api/owners', () => {
    // Assert that the users we get from this call to getUsers()
    // should be our set of test users. Because we're subscribing
    // to the result of getUsers(), this won't actually get
    // checked until the mocked HTTP request 'returns' a response.
    // This happens when we call req.flush(testUsers) a few lines
    // down.
    ownerService.getOwners().subscribe(
      owners => expect(owners).toBe(testOwners)
    );

    // Specify that (exactly) one request will be made to the specified URL.
    const req = httpTestingController.expectOne(ownerService.ownerUrl);
    // Check that the request made to that URL was a GET request.
    expect(req.request.method).toEqual('GET');
    // Specify the content of the response to that request. This
    // triggers the subscribe above, which leads to that check
    // actually being performed.
    req.flush(tests);
  });*/

  it('getOwners() calls api/owners with filter parameter \'admin\'', () => {

    ownerService.getOwners({ name: 'Chris' }).subscribe(
      owners => expect(owners).toBe(testOwners)
    );

    // Specify that (exactly) one request will be made to the specified URL with the role parameter.
    const req = httpTestingController.expectOne(
      (request) => request.url.startsWith(ownerService.ownerUrl) && request.params.has('name')
    );

    // Check that the request made to that URL was a GET request.
    expect(req.request.method).toEqual('GET');

    // Check that the role parameter was 'admin'
    expect(req.request.params.get('name')).toEqual('Chris');

    req.flush(testOwners);
  });

  it('getOwners() calls api/owners with filter parameter \'email\'', () => {

    ownerService.getOwners({ email: 'mars@this.that' }).subscribe(
      owners => expect(owners).toBe(testOwners)
    );

    // Specify that (exactly) one request will be made to the specified URL with the role parameter.
    const req = httpTestingController.expectOne(
      (request) => request.url.startsWith(ownerService.ownerUrl) && request.params.has('email')
    );

    // Check that the request made to that URL was a GET request.
    expect(req.request.method).toEqual('GET');

    // Check that the role parameter was 'admin'
    expect(req.request.params.get('email')).toEqual('mars@this.that');

    req.flush(testOwners);
  });

  it('getOwners() calls api/owners with multiple filter parameters', () => {

    ownerService.getOwners({ name: 'william', building: 'Humanities', officeNumber: '111' }).subscribe(
      owners => expect(owners).toBe(testOwners)
    );

    // Specify that (exactly) one request will be made to the specified URL with the role parameter.
    const req = httpTestingController.expectOne(
      (request) => request.url.startsWith(ownerService.ownerUrl)
        && request.params.has('name') && request.params.has('building') && request.params.has('officenumber')
    );

    // Check that the request made to that URL was a GET request.
    expect(req.request.method).toEqual('GET');

    // Check that the role parameters are correct
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

 /* This must be implemented later
   it('addUser() calls api/users/new', () => {

    userService.addUser(testUsers[1]).subscribe(
      id => expect(id).toBe('testid')
    );

    const req = httpTestingController.expectOne(userService.userUrl + '/new');

    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(testUsers[1]);

    req.flush({id: 'testid'});
  });*/
});
