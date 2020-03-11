import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

import { Note, NoteStatus } from '../notes/note';
import { OnInit, Component, OnDestroy, SecurityContext } from '@angular/core';
import { OwnerService } from './owner.service';
import { Owner } from './owner';
import { Subscription } from 'rxjs';
import { NoteService } from '../notes/note.service';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-owner-page-component',
  templateUrl: 'owner-page.component.html',
  styleUrls: ['owner-page.component.scss'],
  providers: []
})

export class OwnerPageComponent implements OnInit, OnDestroy {

  constructor(private ownerService: OwnerService, private noteService: NoteService,
              private route: ActivatedRoute, private sanitizer: DomSanitizer) { }

  public notes: Note[];

  owner: Owner;
  id: string;

  getNotesSub: Subscription;
  calendarUrl = this.sanitizer.sanitize(SecurityContext.RESOURCE_URL,
    'https://calendar.google.com/calendar/embed?mode=week&src=' + this.owner.email);

  public serverFilteredNotes: Note[];
  public filteredNotes: Note[];

  public noteStatus: NoteStatus;
  public noteAddDate: Date;
  public noteExpireDate: Date;
  public noteBody: string;

  public getNotesFromServer(): void {
    this.unsub();
    this.getNotesSub = this.noteService.getNotesByOwner(
      this.id,{
        status: this.noteStatus,
        body: this.noteBody
      }).subscribe(returnedNotes => {
        this.serverFilteredNotes = returnedNotes;
        this.updateFilter();
      }, err => {
        console.log(err);
      });
  }

  public updateFilter(): void {
    this.filteredNotes = this.noteService.filterNotes(
      this.serverFilteredNotes,
      {
        addDate: this.noteAddDate,
        expireDate: this.noteExpireDate
      });
}

  ngOnInit(): void {
    // Subscribe owner's notes
    this.route.paramMap.subscribe((pmap) => {
      this.id = pmap.get('id');
      if (this.getNotesSub) {
        this.getNotesSub.unsubscribe();
      }
      this.getNotesSub = this.noteService.getNotesByOwner(this.id).subscribe(notes => this.notes = notes);
      this.ownerService.getOwnerById(this.id).subscribe(owners => this.owner = owners);
    });
  }



  ngOnDestroy(): void {
    if (this.getNotesSub) {
      this.getNotesSub.unsubscribe();
    }
  }

  unsub(): void {
    if (this.getNotesSub) {
      this.getNotesSub.unsubscribe();
    }
  }

}
