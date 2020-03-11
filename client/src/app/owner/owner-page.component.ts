import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

import { Note, NoteStatus } from '../notes/note';
import { OnInit, Component, OnDestroy, SecurityContext } from '@angular/core';
import { OwnerService } from './owner.service';
import { Owner } from './owner';
import { Subscription, forkJoin } from 'rxjs';
import { NoteService } from '../notes/note.service';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { HttpParameterCodec } from "@angular/common/http";
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
  getOwnerSub: Subscription;
  public testEmail = 'robi1467@morris.umn.edu';
  public testUrl ='https://calendar.google.com/calendar/embed?src=robi1467%40morris.umn.edu';

  //calendarUrl = this.sanitizer.sanitize(SecurityContext.RESOURCE_URL, 'https://calendar.google.com/calendar/embed?mode=week&src='
   //+ 'robi1467%40morris.umn.edu' +
   //'&ctz=America%2FChicago');

  public calendarUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.testUrl);

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
    });
    if (this.getOwnerSub) {
        this.getOwnerSub.unsubscribe();
      }
    this.getOwnerSub = this.ownerService.getOwnerById(this.id).subscribe(owner => this.owner = owner);
  //  console.log(this.owner.email);
  }



  ngOnDestroy(): void {
    if (this.getNotesSub) {
      this.getNotesSub.unsubscribe();
    }
    if (this.getOwnerSub) {
      this.getOwnerSub.unsubscribe();
    }
  }

  unsub(): void {
    if (this.getNotesSub) {
      this.getNotesSub.unsubscribe();
    }

    if (this.getOwnerSub) {
      this.getOwnerSub.unsubscribe();
    }
  }

}
