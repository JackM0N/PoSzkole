//General
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
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
import { MatCard, MatCardModule } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuModule } from '@angular/material/menu'

//App
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

//Components
import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';
import { RequestListComponent } from './components/teacher/request/request-list.component';
import { RequestAdmittedListComponent } from './components/teacher/request/request-admitted-list.component';
import { RequestFormComponent } from './components/manager/requests/request-form.component';
import { AuthInterceptor } from './services/auth.interceptor';
import { PopUpDialogComponent } from './components/shared/pop-up/pop-up-dialog.component';
import { AdmitRequestPopUpComponent } from './components/teacher/request/admit-request-popup.component';
import { ScheduleComponent } from './components/shared/schedule/schedule.component';
import { StudentScheduleComponent } from './components/student/schedule/student-schedule.component';
import { CommonModule } from '@angular/common';
import { ClassDetailsComponent } from './components/shared/schedule/class-details.component';
import { CancelScheduleComponent } from './components/student/schedule/cancel-schedule.component';
import { TeacherScheduleComponent } from './components/teacher/schedule/teacher-schedule.component';
import { EditClassComponent } from './components/shared/schedule/edit-class.component';
import { ReserveRoomComponent } from './components/teacher/schedule/reserve-room.component';
import { AttendanceComponent } from './components/teacher/schedule/attendance.component';
import { AccountComponent } from './components/shared/profile/profile.component';
import { MyAccountComponent } from './components/shared/profile/my-profile.component';
import { UserAccountComponent } from './components/shared/profile/user-profile.component';
import { EditUserComponent } from './components/shared/profile/edit-user.component';
import { EditSubjectsComponent } from './components/shared/profile/edit-subjects.component';
import { CheckRoomAvailiabilityComponent } from './components/teacher/schedule/check-room-availability.component';
import { StudentCoursesComponent } from './components/student/courses/student-courses.component';
import { AvailableCoursesComponent } from './components/shared/courses/available-courses.component';
import { StudentAttendanceComponent } from './components/student/attendance/student-attendance.component';
import { StudentAbsenceComponent } from './components/student/attendance/student-absence.component';
import { StudentsComponent } from './components/manager/students/students.component';
import { TeachersComponent } from './components/manager/teachers/teachers.component';
import { UserBusyDaysComponent } from './components/shared/profile/user-busy-days.component';
import { EditBusyDaysComponent } from './components/shared/profile/edit-busy-days.component';
import { RegisterTeacherComponent } from './components/manager/teachers/register-teacher.component';
import { RegisterStudentComponent } from './components/manager/students/register-student.component';
import { ManagerCoursesComponent } from './components/manager/courses/manager-courses.component';


@NgModule({ declarations: [
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
        CancelScheduleComponent,
        TeacherScheduleComponent,
        EditClassComponent,
        ReserveRoomComponent,
        AttendanceComponent,
        AccountComponent,
        MyAccountComponent,
        UserAccountComponent,
        EditUserComponent,
        EditSubjectsComponent,
        CheckRoomAvailiabilityComponent,
        StudentCoursesComponent,
        AvailableCoursesComponent,
        StudentAttendanceComponent,
        StudentAbsenceComponent,
        StudentsComponent,
        TeachersComponent,
        UserBusyDaysComponent,
        EditBusyDaysComponent,
        RegisterTeacherComponent,
        RegisterStudentComponent,
        ManagerCoursesComponent
    ],
    bootstrap: [AppComponent], 
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule,
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
        MatCardModule,
        MatIcon,
        MatMenu,
        MatMenuModule
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
        },
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class AppModule { }
