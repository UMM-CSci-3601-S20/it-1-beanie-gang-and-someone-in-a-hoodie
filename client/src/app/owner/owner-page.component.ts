import { OnInit, Component, OnDestroy } from '@angular/core';
import { Note } from '../notes/note';
import { OwnerService } from './owner.service';
import { Owner } from './owner';
import { Subscription } from 'rxjs';
import { NoteService } from '../notes/note.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-owner-page-component',
  templateUrl: 'owner-page.component.html',
  styleUrls: ['owner-page.component.scss'],
  providers: []
})

export class OwnerPageComponent implements OnInit, OnDestroy {

  constructor(private ownerService: OwnerService,
              private noteService: NoteService, private route: ActivatedRoute) { }

  // Keys: 'active', 'template', 'deleted', 'draft'
  public notes: Map<string, Note[]>;

  owner: Owner;
  id: string;
  getOwnerSub: Subscription;
  getNotesSub: Subscription;




  ngOnInit(): void {
    // Subscribe to the correct owner and their notes
    this.route.paramMap.subscribe((pmap) => {
      this.id = pmap.get('id');
      if (this.getOwnerSub) {
        this.getOwnerSub.unsubscribe();
      }
      this.getOwnerSub = this.ownerService.getOwnerById(this.id).subscribe(owner => this.owner = owner);

      if (this.getNotesSub) {
        this.getNotesSub.unsubscribe();
      }
      this.getNotesSub = this.noteService.getNotesByOwner(this.id).subscribe(notes => this.notes = notes);
    });
  }



  ngOnDestroy(): void {
    if (this.getOwnerSub) {
      this.getOwnerSub.unsubscribe();
    }

    if (this.getNotesSub) {
      this.getNotesSub.unsubscribe();
    }
  }

}
