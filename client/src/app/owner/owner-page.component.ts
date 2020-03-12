import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

import { Note, NoteStatus } from '../notes/note';
import { OnInit, Component, OnDestroy, SecurityContext } from '@angular/core';
import { OwnerService } from './owner.service';
import { Owner } from './owner';
import { Subscription, forkJoin } from 'rxjs';
import { NoteService } from '../notes/note.service';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { HttpParameterCodec } from "@angular/common/http";
import { async } from '@angular/core/testing';
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
  public serverFilteredNotes: Note[];
  public filteredNotes: Note[];
  public GcalURL: string;

  owner: Owner;
  id: string;

  getNotesSub: Subscription;
  getOwnerSub: Subscription;

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

  public createGmailConnection(ownerEmail: string): void {
    let gmailUrl = ownerEmail.replace('@', '%40'); // Convert owner e-mail to acceptable format for connection to gCalendar
    console.log('BEING CALLED');
    gmailUrl = 'https://calendar.google.com/calendar/embed?src=' + gmailUrl; // Connection string
    this.GcalURL = gmailUrl; // Set the global connection string
  }
  public returnSafeLink(): SafeResourceUrl{
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.GcalURL);  // Return a "safe" link to gCalendar
  }
  public getName(): string {
    return this.owner.name;
  }
  public getBuilding(): string {
    return this.owner.building;
  }
  public getOfficeNumber(): string {
    return this.owner.officeNumber;
  }
  public getEmail(): string {
    return this.owner.email;
  }

  ngOnInit(): void {
    // Subscribe owner's notes
    this.route.paramMap.subscribe((pmap) => {
      this.id = pmap.get('id');
      if (this.getNotesSub){
        this.getNotesSub.unsubscribe();
      }
      this.getNotesSub = this.noteService.getNotesByOwner(this.id).subscribe( notes => this.notes = notes);
      if (this.getOwnerSub) {
        this.getOwnerSub.unsubscribe();
      }
      this.getOwnerSub = this.ownerService.getOwnerById(this.id).subscribe( async (owner: Owner) => {
      this.owner = owner;
      this.createGmailConnection(this.owner.email);
    });
  });
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
