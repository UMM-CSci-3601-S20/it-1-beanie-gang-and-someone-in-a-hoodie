import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { OwnerListComponent } from './owner/owner-list.component';
import { AddOwnerComponent } from './owner/add-owner.component';
import { OwnerPageComponent } from './owner/owner-page.component';
import { EditNoteComponent } from './notes/edit-note.component';
import { AddNoteComponent } from './notes/add-note.component';



const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'owners', component: OwnerListComponent},
  {path: 'owners/new', component: AddOwnerComponent},
  {path: 'owners/:id', component: OwnerPageComponent},
  {path: 'notes', component: OwnerPageComponent},
  {path: 'notes/new', component: AddNoteComponent},
  {path: 'notes/edit', component: EditNoteComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
