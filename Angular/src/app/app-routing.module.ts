import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

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

const routes: Routes = [
  //auth
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegistrationComponent},

  //profile
  {path: 'profile/:id', component: UserAccountComponent},
  {path: 'my-profile', component: MyAccountComponent},

  //manager
  {path: 'request-form', component: RequestFormComponent},
  {path: 'teachers', component: TeachersComponent},
  {path: 'students', component: StudentsComponent},

  //teacher
  {path: 'request-list', component: RequestListComponent},
  {path: 'teacher-schedule', component: TeacherScheduleComponent},
  
  //student
  {path: 'student-schedule', component: StudentScheduleComponent},
  {path: 'student-courses', component: StudentCoursesComponent},
  {path: 'attendance', component: StudentAttendanceComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
