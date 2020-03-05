import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';

import {MatButtonModule} from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import {MatDividerModule} from '@angular/material/divider';
import {MatRadioModule} from '@angular/material/radio';
import {MatSnackBarModule} from '@angular/material/snack-bar';

// Home
import { HomeComponent } from './home/home.component';

// User
import { UserListComponent } from './users/user-list.component';
import { UserService } from './users/user.service';
import { UserProfileComponent } from './users/user-profile.component';
import { AddUserComponent } from './users/add-user.component';
import { UserCardComponent } from './users/user-card.component';


// Faculty
import { FacultyListComponent } from './faculty/faculty-list.component';
import { FacultyService } from './faculty/faculty.service';
import { AddFacultyComponent } from './faculty/add-faculty.component';
import { FacultyNoticesComponent } from './faculty/faculty-notices.component';
import { FacultyPageComponent } from './faculty/faculty-page.component';

// Note
import { NoteListComponent } from './notes/note-list.component';
import { NoteService } from './notes/note.service';
import { EditNoteComponent } from './notes/edit-note.component';
import { NotePageComponent } from './notes/note-page.component';

// Other
import { HttpClientModule } from '@angular/common/http';
import { LayoutModule } from '@angular/cdk/layout';

const MATERIAL_MODULES: any[] = [
  MatListModule,
  MatButtonModule,
  MatIconModule,
  MatToolbarModule,
  MatCardModule,
  MatMenuModule,
  MatSidenavModule,
  MatInputModule,
  MatExpansionModule,
  MatTooltipModule,
  MatSelectModule,
  MatOptionModule,
  MatFormFieldModule,
  MatDividerModule,
  MatRadioModule,
  MatSnackBarModule
];

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    UserListComponent,
    UserCardComponent,
    UserProfileComponent,
    AddUserComponent,
    FacultyListComponent,
    AddFacultyComponent,
    FacultyNoticesComponent,
    FacultyPageComponent,
    NoteListComponent,
    EditNoteComponent,
    NotePageComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    HttpClientModule,
    MATERIAL_MODULES,
    LayoutModule,
  ],
  providers: [
    UserService,
    FacultyService,
    NoteService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
