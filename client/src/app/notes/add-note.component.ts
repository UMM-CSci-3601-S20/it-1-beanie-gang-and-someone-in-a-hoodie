import { Component, OnInit, Input} from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Note } from './note';
import { NoteService } from './note.service';
import { Owner } from '../owner/owner';


@Component({
  selector: 'app-add-note',
  templateUrl: './add-note.component.html',
  styleUrls: [ ]
})
export class AddNoteComponent implements OnInit {

  @Input() owner_id: string = null;

  addNoteForm: FormGroup;
  note: Note;
  constructor(private fb: FormBuilder,
              private noteService: NoteService, private snackBar: MatSnackBar, private router: Router, ) {
  }

  add_note_validation_messages = {
    status: [
      {type: 'required', message: 'Status is required'},
      {type: 'pattern', message: 'Must be active, draft or template'}, // don't want to create a deleted message
    ],

    body: [,
      {type: 'required', message: 'Body is required'},
      {type: 'maxLength', message: 'Cannot exceed 1000 characters'}
    ],

    expireDate: [
      { type: 'required', message: 'Role is required' },
      { type: 'pattern', message: 'Most be formatted correctly' }, // up for change I have no idea how this works
    ]
  };


  createForms() {

    // add note form validations
    this.addNoteForm = this.fb.group({
      status: new FormControl('active', Validators.compose([
        Validators.required,
        Validators.pattern('^(active|draft|template)$'),
      ])),

      body: new FormControl('', Validators.compose([
        Validators.required,
        Validators.max(1000),
      ])),


      expireDate: new FormControl('2021-03-06T22:03:38+0000', Validators.compose([])),
    });

  }

  ngOnInit() {
    this.createForms();
  }


  submitForm() {
    const noteToAdd: Note = this.addNoteForm.value;
    noteToAdd.ownerID = this.owner_id; // get owner ID from somewhere, put here
    noteToAdd.addDate = new Date();

    this.noteService.addNewNote(noteToAdd).subscribe(newID => {
      this.snackBar.open('Added Note ', null, {
        duration: 2000,
      });
      this.router.navigate(['/owner/', this.owner_id]);
    }, err => {
      this.snackBar.open('Failed to add the note', null, {
        duration: 2000,
      });
    });
  }

}
