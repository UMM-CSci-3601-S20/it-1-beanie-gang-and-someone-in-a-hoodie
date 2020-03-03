import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { UserListComponent } from './users/user-list.component';
import { UserProfileComponent } from './users/user-profile.component';
import { AddUserComponent } from './users/add-user.component';
import { FacultyListComponent } from './faculty/faculty-list.component';
import { AddFacultyComponent } from './faculty/add-faculty.component';
import { FacultyPageComponent } from './faculty/faculty-page.component';
import { NoteListComponent } from './notes/note-list.component';
import { EditNoteComponent } from './notes/edit-note.component';
import { NotePageComponent } from './notes/note-page.component';



const routes: Routes = [
  {path: '', component: HomeComponent},
  // {path: 'users', component: UserListComponent},
  // {path: 'users/new', component: AddUserComponent},
  // {path: 'users/:id', component: UserProfileComponent}
  {path: 'faculty', component: FacultyListComponent},
  {path: 'faculty/new', component: AddFacultyComponent},
  {path: 'faculty/:id', component: FacultyPageComponent},
  {path: 'notes', component: NoteListComponent},
  {path: 'notes/new', component: EditNoteComponent},
  {path: 'notes/:id', component: NotePageComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
