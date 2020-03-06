import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatOptionModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Observable } from 'rxjs';
import { MockOwnerService } from '../../testing/owner.service.mock';
import { Owner } from './owner';
import { OwnerListComponent } from './owner-list.component';
import { OwnerService } from './owner.service';
import { MatIconModule } from '@angular/material/icon';

const COMMON_IMPORTS: any[] = [
  FormsModule,
  MatCardModule,
  MatFormFieldModule,
  MatSelectModule,
  MatOptionModule,
  MatButtonModule,
  MatInputModule,
  MatExpansionModule,
  MatTooltipModule,
  MatListModule,
  MatDividerModule,
  MatRadioModule,
  MatIconModule,
  BrowserAnimationsModule,
  RouterTestingModule,
];

describe('Owner list', () => {

  let ownerList: OwnerListComponent;
  let fixture: ComponentFixture<OwnerListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [COMMON_IMPORTS],
      declarations: [OwnerListComponent],
      // providers:    [ OwnerService ]  // NO! Don't provide the real service!
      // Provide a test-double instead
      providers: [{ provide: OwnerService, useValue: new MockOwnerService() }]
    });
  });

  beforeEach(async(() => {
    TestBed.compileComponents().then(() => {
      fixture = TestBed.createComponent(OwnerListComponent);
      ownerList = fixture.componentInstance;
      fixture.detectChanges();
    });
  }));

  it('contains all the owners', () => {
    expect(ownerList.serverFilteredOwners.length).toBe(3);
  });

  it('contains a owner named \'Chris\'', () => {
    expect(ownerList.serverFilteredOwners.some((owner: Owner) => owner.name === 'Chris')).toBe(true);
  });

  it('contain a owner named \'Jamie\'', () => {
    expect(ownerList.serverFilteredOwners.some((owner: Owner) => owner.name === 'Jamie')).toBe(true);
  });

  it('doesn\'t contain a owner named \'Santa\'', () => {
    expect(ownerList.serverFilteredOwners.some((owner: Owner) => owner.name === 'Santa')).toBe(false);
  });
});

describe('Misbehaving Owner List', () => {
  let ownerList: OwnerListComponent;
  let fixture: ComponentFixture<OwnerListComponent>;

  let ownerServiceStub: {
    getOwners: () => Observable<Owner[]>;
    getOwnersFiltered: () => Observable<Owner[]>;
  };

  beforeEach(() => {
    // stub OwnerService for test purposes
    ownerServiceStub = {
      getOwners: () => new Observable(observer => {
        observer.error('Error-prone observable');
      }),
      getOwnersFiltered: () => new Observable(observer => {
        observer.error('Error-prone observable');
      })
    };

    TestBed.configureTestingModule({
      imports: [COMMON_IMPORTS],
      declarations: [OwnerListComponent],
      // providers:    [ OwnerService ]  // NO! Don't provide the real service!
      // Provide a test-double instead
      providers: [{ provide: OwnerService, useValue: ownerServiceStub }]
    });
  });

  beforeEach(async(() => {
    TestBed.compileComponents().then(() => {
      fixture = TestBed.createComponent(OwnerListComponent);
      ownerList = fixture.componentInstance;
      fixture.detectChanges();
    });
  }));

  it('generates an error if we don\'t set up a OwnListService', () => {
    // Since the observer throws an error, we don't expect owns to be defined.
    expect(ownerList.serverFilteredOwners).toBeUndefined();
  });
});
