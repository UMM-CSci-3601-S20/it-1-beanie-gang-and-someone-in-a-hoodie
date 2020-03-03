import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { User } from './user';
import { UserService } from './user.service';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.scss']
})
export class AddUserComponent implements OnInit {

  addUserForm: FormGroup;

  user: User;

  constructor(private fb: FormBuilder, private userService: UserService, private snackBar: MatSnackBar, private router: Router) {
  }

  // not sure if this name is magical and making it be found or if I'm missing something,
  // but this is where the red text that shows up (when there is invalid input) comes from
  add_user_validation_messages = {
    name: [
      {type: 'required', message: 'Name is required'},
      {type: 'minlength', message: 'Name must be at least 2 characters long'},
      {type: 'maxlength', message: 'Name cannot be more than 50 characters long'},
      {type: 'pattern', message: 'Name must contain only numbers and letters'},
      {type: 'existingName', message: 'Name has already been taken'}
    ],

    age: [
      {type: 'pattern', message: 'Age must be a number'},
      {type: 'min', message: 'Age must be at least 15'},
      {type: 'max', message: 'Age may not be greater than 200'},
      {type: 'required', message: 'Age is required'}
    ],

    email: [
      {type: 'email', message: 'Email must be formatted properly'},
      {type: 'required', message: 'Email is required'}
    ],

    role: [
      { type: 'required', message: 'Role is required' },
      { type: 'pattern', message: 'Role must be Admin, Editor, or Viewer' },
    ]
  };

  createForms() {

    // add user form validations
    this.addUserForm = this.fb.group({
      // We allow alphanumeric input and limit the length for name.
      name: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(2),
        // In the real world you'd want to be very careful about having
        // an upper limit like this because people can sometimes have
        // very long names. This demonstrates that it's possible, though,
        // to have maximum length limits.
        Validators.maxLength(50),
        Validators.pattern('^[A-Za-z0-9\\s]+[A-Za-z0-9\\s]+$(\\.0-9+)?'),
        (fc) => {
          if (fc.value.toLowerCase() === 'abc123' || fc.value.toLowerCase() === '123abc') {
            return ({existingName: true});
          } else {
            return null;
          }
        },
      ])),

      // Since this is for a company, we need workers to be old enough to work, and probably not older than 200.
      age: new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern('^[0-9]+[0-9]?'),
        Validators.min(15),
        Validators.max(200),
      ])),

      // We don't care much about what is in the company field, so we just add it here as part of the form
      // without any particular validation.
      company: new FormControl(),

      // We don't need a special validator just for our app here, but there is a default one for email.
      // We will require the email, though.
      email: new FormControl('', Validators.compose([
        Validators.required,
        Validators.email,
      ])),

      role: new FormControl('viewer', Validators.compose([
        Validators.required,
        Validators.pattern('^(admin|editor|viewer)$'),
      ])),
    });

  }

  ngOnInit() {
    this.createForms();
  }


  submitForm() {
    this.userService.addUser(this.addUserForm.value).subscribe(newID => {
      this.snackBar.open('Added User ' + this.addUserForm.value.name, null, {
        duration: 2000,
      });
      this.router.navigate(['/users/', newID]);
    }, err => {
      this.snackBar.open('Failed to add the user', null, {
        duration: 2000,
      });
    });
  }

}
