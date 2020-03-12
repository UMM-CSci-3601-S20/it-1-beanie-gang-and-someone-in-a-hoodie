import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, NgForm, ReactiveFormsModule, FormGroup, AbstractControl } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { MockOwnerService } from 'src/testing/owner.service.mock';
import { AddOwnerComponent } from './add-owner.component';
import { OwnerService } from './owner.service';

describe('AddOwnerComponent', () => {
  let addOwnerComponent: AddOwnerComponent;
  let addOwnerForm: FormGroup;
  let calledClose: boolean;
  let fixture: ComponentFixture<AddOwnerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        BrowserAnimationsModule,
        RouterTestingModule
      ],
      declarations: [AddOwnerComponent],
      providers: [{ provide: OwnerService, useValue: new MockOwnerService() }]
    }).compileComponents().catch(error => {
      expect(error).toBeNull();
    });
  }));

  beforeEach(() => {
    calledClose = false;
    fixture = TestBed.createComponent(AddOwnerComponent);
    addOwnerComponent = fixture.componentInstance;
    addOwnerComponent.ngOnInit();
    fixture.detectChanges();
    addOwnerForm = addOwnerComponent.addOwnerForm;
    expect(addOwnerForm).toBeDefined();
    expect(addOwnerForm.controls).toBeDefined();
  });

  // Not terribly important; if the component doesn't create
  // successfully that will probably blow up a lot of things.
  // Including it, though, does give us confidence that our
  // our component definitions don't have errors that would
  // prevent them from being successfully constructed.
  it('should create the component and form', () => {
    expect(addOwnerComponent).toBeTruthy();
    expect(addOwnerForm).toBeTruthy();
  });

  // Confirms that an initial, empty form is *not* valid, so
  // people can't submit an empty form.
  it('form should be invalid when empty', () => {
    expect(addOwnerForm.valid).toBeFalsy();
  });

  describe('The name field', () => {
    let nameControl: AbstractControl;

    beforeEach(() => {
      nameControl = addOwnerComponent.addOwnerForm.controls[`name`];
    });

    it('should not allow empty names', () => {
      nameControl.setValue('');
      expect(nameControl.valid).toBeFalsy();
    });

    it('should be fine with "Captain Kirk"', () => {
      nameControl.setValue('Captain Kirk');
      expect(nameControl.valid).toBeTruthy();
    });

    // it('should fail on single character names', () => {
    //   nameControl.setValue('x');
    //   expect(nameControl.valid).toBeFalsy();
    //   // Annoyingly, Angular uses lowercase 'l' here
    //   // when it's an upper case 'L' in `Validators.minLength(2)`.
    //   expect(nameControl.hasError('minlength')).toBeTruthy();
    // });

    // In the real world, you'd want to be pretty careful about
    // setting upper limits on things like name lengths just
    // because there are people with really long names.
    it('should fail on really long names', () => {
      nameControl.setValue('x'.repeat(100));
      expect(nameControl.valid).toBeFalsy();
      // Annoyingly, Angular uses lowercase 'l' here
      // when it's an upper case 'L' in `Validators.maxLength(2)`.
      expect(nameControl.hasError('maxlength')).toBeTruthy();
    });

    it('should not allow a name to contain a symbol', () => {
      nameControl.setValue('bad@email.com');
      expect(nameControl.valid).toBeFalsy();
      expect(nameControl.hasError('pattern')).toBeTruthy();
    });

    it('should allow digits in the name', () => {
      nameControl.setValue('ProQUickScopeMonster360');
      expect(nameControl.valid).toBeTruthy();
    });
  });

  describe('The building field', () => {
    let buildingControl: AbstractControl;

    beforeEach(() => {
      buildingControl = addOwnerComponent.addOwnerForm.controls[`building`];
    });

    it('should not allow empty buildings', () => {
      buildingControl.setValue('');
      expect(buildingControl.valid).toBeFalsy();
    });

    it('should be fine with "The Enterprise"', () => {
      buildingControl.setValue('The Enterprise');
      expect(buildingControl.valid).toBeTruthy();
    });

    it('should fail on single character buildings', () => {
      buildingControl.setValue('x');
      expect(buildingControl.valid).toBeFalsy();
      // Annoyingly, Angular uses lowercase 'l' here
      // when it's an upper case 'L' in `Validators.minLength(2)`.
      expect(buildingControl.hasError('minlength')).toBeTruthy();
    });

    // In the real world, you'd want to be pretty careful about
    // setting upper limits on things like name lengths just
    // because there are people with really long names.
    it('should fail on really long building names', () => {
      buildingControl.setValue('x'.repeat(100));
      expect(buildingControl.valid).toBeFalsy();
      // Annoyingly, Angular uses lowercase 'l' here
      // when it's an upper case 'L' in `Validators.maxLength(2)`.
      expect(buildingControl.hasError('maxlength')).toBeTruthy();
    });

    it('should not allow a building to contain a symbol', () => {
      buildingControl.setValue('bad@email.com');
      expect(buildingControl.valid).toBeFalsy();
      expect(buildingControl.hasError('pattern')).toBeTruthy();
    });

    it('should allow digits in the building', () => {
      buildingControl.setValue('ProQUickScopeMonster360');
      expect(buildingControl.valid).toBeTruthy();
    });
  });

  describe('The email field', () => {
    let emailControl: AbstractControl;

    beforeEach(() => {
      emailControl = addOwnerComponent.addOwnerForm.controls[`email`];
    });

    it('should not allow empty values', () => {
      emailControl.setValue('');
      expect(emailControl.valid).toBeFalsy();
      expect(emailControl.hasError('required')).toBeTruthy();
    });

    it('should accept legal emails', () => {
      emailControl.setValue('conniestewart@ohmnet.com');
      expect(emailControl.valid).toBeTruthy();
    });

    it('should fail without @', () => {
      emailControl.setValue('conniestewart');
      expect(emailControl.valid).toBeFalsy();
      expect(emailControl.hasError('email')).toBeTruthy();
    });
  });

  describe('The office number field', () => {
    let officeNumberControl: AbstractControl;

    beforeEach(() => {
      officeNumberControl = addOwnerComponent.addOwnerForm.controls[`officeNumber`];
    });

    it('should not allow empty office number', () => {
      officeNumberControl.setValue('');
      expect(officeNumberControl.valid).toBeFalsy();
    });

    it('should be fine with "1234a"', () => {
      officeNumberControl.setValue('1234a');
      expect(officeNumberControl.valid).toBeTruthy();
    });

    // In the real world, you'd want to be pretty careful about
    // setting upper limits on things like name lengths just
    // because there are people with really long names.
    it('should fail on really long names', () => {
      officeNumberControl.setValue('x'.repeat(100));
      expect(officeNumberControl.valid).toBeFalsy();
      // Annoyingly, Angular uses lowercase 'l' here
      // when it's an upper case 'L' in `Validators.maxLength(2)`.
      expect(officeNumberControl.hasError('maxlength')).toBeTruthy();
    });

    it('should not allow a name to contain a symbol', () => {
      officeNumberControl.setValue('bad@email.com');
      expect(officeNumberControl.valid).toBeFalsy();
      expect(officeNumberControl.hasError('pattern')).toBeTruthy();
    });
  });

});
