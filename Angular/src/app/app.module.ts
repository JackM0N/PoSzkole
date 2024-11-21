//General
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { ToastrModule } from 'ngx-toastr';

//Material
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTable, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatCard, MatCardModule } from '@angular/material/card'

//App
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

//Components
import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';
import { RequestListComponent } from './components/teacher/request/request-list.component';
import { RequestAdmittedListComponent } from './components/teacher/request/request-admitted-list.component';
import { RequestFormComponent } from './components/manager/request-form/request-form.component';
import { AuthInterceptor } from './services/auth.interceptor';
import { PopUpDialogComponent } from './components/pop-up/pop-up-dialog.component';
import { AdmitRequestPopUpComponent } from './components/teacher/request/admit-request-popup.component';
import { ScheduleComponent } from './components/shared/schedule/schedule.component';
import { StudentScheduleComponent } from './components/student/schedule/student-schedule.component';
import { CommonModule } from '@angular/common';
import { ClassDetailsComponent } from './components/shared/schedule/class-details.component';
import { CancelScheduleComponent } from './components/student/schedule/cancel-schedule.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    RequestListComponent,
    RequestFormComponent,
    RequestAdmittedListComponent,
    PopUpDialogComponent,
    AdmitRequestPopUpComponent,
    ScheduleComponent,
    StudentScheduleComponent,
    ClassDetailsComponent,
    CancelScheduleComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    CommonModule,
    ToastrModule.forRoot({
      timeOut: 5000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    }),

    MatInputModule,
    MatAutocompleteModule,
    MatSelectModule,
    MatCheckboxModule,
    MatButtonModule,
    MatNativeDateModule,
    MatDatepickerModule,
    MatPaginator,
    MatTable,
    MatTableModule,
    MatSort,
    MatSortModule,
    MatDialogModule,
    MatCard,
    MatCardModule
  ],
  providers: [
    provideAnimationsAsync(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: MAT_DATE_LOCALE,
      useValue: 'pl-PL'
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
