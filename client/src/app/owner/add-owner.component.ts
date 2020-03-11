import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Owner } from './owner';
import { OwnerService } from './owner.service';

@Component({
  selector: 'app-add-owner',
  templateUrl: './add-owner.component.html',
  styleUrls: []
})
export class AddOwnerComponent implements OnInit {

  addOwnerForm: FormGroup;

  owner: Owner;

  constructor(private fb: FormBuilder, private ownerService: OwnerService, private snackBar: MatSnackBar, private router: Router) {
  }

  // not sure if this name is magical and making it be found or if I'm missing something,
  // but this is where the red text that shows up (when there is invalid input) comes from
  add_owner_validation_messages = {
    name: [
      {type: 'required', message: 'Name is required'},
      {type: 'minlength', message: 'Name must be at least 2 characters long'},
      {type: 'maxlength', message: 'Name cannot be more than 50 characters long'},
      {type: 'pattern', message: 'Name must contain only numbers and letters'},
      // {type: 'existingName', message: 'Name has already been taken'}
    ],

    email: [
      {type: 'email', message: 'Email must be formatted properly'},
      {type: 'required', message: 'Email is required'}
    ],

    building: [
      { type: 'required', message: 'building is required' },
      {type: 'minlength', message: 'Building must be at least 2 characters long'},
      {type: 'maxlength', message: 'Building cannot be more than 50 characters long'},
      {type: 'pattern', message: 'Building must contain only numbers and letters'},
    ],
    officeNumber: [
      { type: 'required', message: 'Office number is required' },
      {type: 'minlength', message: 'Office number must be at least 1 characters long'},
      {type: 'maxlength', message: 'Office number cannot be more than 25 characters long'},
      {type: 'pattern', message: 'Office number must contain only numbers and letters'},
    ]
  };

  createForms() {

    // add owner form validations
    this.addOwnerForm = this.fb.group({
      // We allow alphanumeric input and limit the length for name.
      name: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern('^[A-Za-z0-9\\s]+[A-Za-z0-9\\s]+$(\\.0-9+)?'),
      ])),
      // We don't need a special validator just for our app here, but there is a default one for email.
      // We will require the email, though.
      email: new FormControl('', Validators.compose([
        Validators.required,
        Validators.email,
      ])),

      building: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern('^[A-Za-z0-9\\s]+[A-Za-z0-9\\s]+$(\\.0-9+)?'),
      ])),

      officeNumber: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[A-Za-z0-9\\s]*'),
      ])),

    });

  }

  ngOnInit() {
    this.createForms();
  }


  submitForm() {
    this.ownerService.addOwner(this.addOwnerForm.value).subscribe(newID => {
      this.snackBar.open('Added Owner ' + this.addOwnerForm.value.name, null, {
        duration: 2000,
      });
      this.router.navigate(['/owners/', newID]);
    }, err => {
      this.snackBar.open('Failed to add the owner', null, {
        duration: 2000,
      });
    });
  }

}
