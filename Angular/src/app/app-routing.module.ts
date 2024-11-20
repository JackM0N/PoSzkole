import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';

import { RequestFormComponent } from './components/manager/request-form/request-form.component';
import { RequestListComponent } from './components/teacher/request/request-list.component';
import { StudentScheduleComponent } from './components/student/schedule/student-schedule.component';

const routes: Routes = [
  //auth
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegistrationComponent},

  //manager
  {path: 'request-form', component: RequestFormComponent},
  //teacher
  {path: 'request-list', component: RequestListComponent},
  //student
  {path: 'student-schedule', component: StudentScheduleComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
