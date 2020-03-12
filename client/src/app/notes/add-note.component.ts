import { Component, OnInit, Input, SystemJsNgModuleLoader} from '@angular/core';
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


  @Input() owner_id: string;

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
      {type: 'minLength', message: 'Body must no be empty'},
      {type: 'maxLength', message: 'Cannot exceed 1000 characters'}
    ],

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
        Validators.minLength(1),
        Validators.maxLength(1000),
      ])),

    });

  }

  ngOnInit() {
    this.createForms();
  }


  submitForm() {
    const noteToAdd: Note = this.addNoteForm.value;
    //const owner_id = this.router.url.substring(9); // trim off "/notes/new/"
    noteToAdd.ownerID = this.owner_id;
    noteToAdd.addDate = new Date().toISOString();
    this.noteService.addNewNote(noteToAdd).subscribe(newID => {

      this.snackBar.open('Added Note ', null, {
        duration: 2000,
      });
      this.router.navigate(['/owners/', this.owner_id]);
      location.reload();
    }, err => {
      this.snackBar.open('Failed to add the note', null, {
        duration: 2000,
      });
    });
  }

}
