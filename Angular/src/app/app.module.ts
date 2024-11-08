//General
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

//Material
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTable, MatTableModule } from '@angular/material/table';

//App
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

//Components
import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';
import { RequestListComponent } from './components/teacher/request/request-list.component';
import { RequestFormComponent } from './components/manager/request-form/request-form.component';
import { AuthInterceptor } from './services/auth.interceptor';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    RequestListComponent,
    RequestFormComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,

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
  ],
  providers: [
    provideAnimationsAsync(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
