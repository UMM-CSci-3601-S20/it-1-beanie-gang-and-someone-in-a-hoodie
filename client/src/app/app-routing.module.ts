import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { UserListComponent } from './users/user-list.component';
import { UserProfileComponent } from './users/user-profile.component';
import { AddUserComponent } from './users/add-user.component';
import { OwnerListComponent } from './owner/owner-list.component';
import { AddOwnerComponent } from './owner/add-owner.component';
import { OwnerPageComponent } from './owner/owner-page.component';
import { NoteListComponent } from './notes/note-list.component';
import { EditNoteComponent } from './notes/edit-note.component';
import { NotePageComponent } from './notes/note-page.component';



const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'users', component: UserListComponent},
  {path: 'users/new', component: AddUserComponent},
  {path: 'users/:id', component: UserProfileComponent},
  {path: 'owners', component: OwnerListComponent},
  {path: 'owners/new', component: AddOwnerComponent},
  {path: 'owners/:id', component: OwnerPageComponent},
  {path: 'notes', component: NoteListComponent},
  {path: 'notes/new', component: EditNoteComponent},
  {path: 'notes/:id', component: NotePageComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
