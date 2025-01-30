import { NgModule } from '@angular/core';
import { RouterModule,Routes } from '@angular/router';

import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';

import { RequestFormComponent } from './components/manager/requests/request-form.component';
import { RequestListComponent } from './components/teacher/request/request-list.component';
import { StudentScheduleComponent } from './components/student/schedule/student-schedule.component';
import { TeacherScheduleComponent } from './components/teacher/schedule/teacher-schedule.component';
import { UserAccountComponent } from './components/shared/profile/user-profile.component';
import { MyAccountComponent } from './components/shared/profile/my-profile.component';
import { StudentCoursesComponent } from './components/student/courses/student-courses.component';
import { StudentAttendanceComponent } from './components/student/attendance/student-attendance.component';
import { StudentsComponent } from './components/manager/students/students.component';
import { TeachersComponent } from './components/manager/teachers/teachers.component';
import { ManagerCoursesComponent } from './components/manager/courses/manager-courses.component';
import { AccessDeniedComponent } from './components/shared/access-denied/access-denied.component';
import { AuthGuard } from './services/auth.guard';

const routes: Routes = [
  //auth
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegistrationComponent },

  //profile
  { path: 'profile/:id', component: UserAccountComponent },
  { path: 'my-profile', component: MyAccountComponent },

  // manager
  { path: 'request-form', component: RequestFormComponent, canActivate: [AuthGuard], data: { roles: ['MANAGER'] } },
  { path: 'teachers', component: TeachersComponent, canActivate: [AuthGuard], data: { roles: ['MANAGER'] } },
  { path: 'students', component: StudentsComponent, canActivate: [AuthGuard], data: { roles: ['MANAGER'] } },
  { path: 'manager-courses', component: ManagerCoursesComponent, canActivate: [AuthGuard], data: { roles: ['MANAGER'] } },

  // teacher
  { path: 'request-list', component: RequestListComponent, canActivate: [AuthGuard], data: { roles: ['TEACHER'] } },
  { path: 'teacher-schedule', component: TeacherScheduleComponent, canActivate: [AuthGuard], data: { roles: ['TEACHER'] } },

  // student
  { path: 'student-schedule', component: StudentScheduleComponent, canActivate: [AuthGuard], data: { roles: ['STUDENT'] } },
  { path: 'student-courses', component: StudentCoursesComponent, canActivate: [AuthGuard], data: { roles: ['STUDENT'] } },
  { path: 'attendance', component: StudentAttendanceComponent, canActivate: [AuthGuard], data: { roles: ['STUDENT'] } },

  //auth guard
  { path: 'access-denied', component: AccessDeniedComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
