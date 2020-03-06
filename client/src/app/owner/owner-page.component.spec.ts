import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRouteStub } from '../../testing/activated-route-stub';

// Owner imports
import { Owner } from './owner';
import { OwnerService } from './owner.service';
import { OwnerPageComponent } from './owner-page.component';
import { MockOwnerService } from '../../testing/owner.service.mock';

// Note imports
import { Note } from '../notes/note';
import { NoteService } from '../notes/note.service';
import { MockNoteService } from '../../testing/note.service.mock';


describe('OwnerPageComponent', () => {
  let component: OwnerPageComponent;
  let fixture: ComponentFixture<OwnerPageComponent>;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MatCardModule
      ],
      declarations: [OwnerPageComponent],
      providers: [
        {provide: OwnerService, useValue: new MockOwnerService()},
        {provide: NoteService, useValue: new MockNoteService()},
        {provide: activatedRoute, useValue: activatedRoute}
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnerPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
})
