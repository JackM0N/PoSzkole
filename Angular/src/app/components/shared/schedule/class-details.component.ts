import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { CancelScheduleComponent } from '../../student/schedule/cancel-schedule.component';
import { JwtHelperService } from '@auth0/angular-jwt';
import { AuthService } from '../../../services/auth.service';
import { Role } from '../../../models/role.model';
import { TutoringClassService } from '../../../services/tutoring-class.service';
import { SimplifiedUser } from '../../../models/simplified-user.model';
import { EditClassComponent } from './edit-class.component';
import { ReserveRoomComponent } from '../../teacher/schedule/reserve-room.component';
import { AttendanceService } from '../../../services/attendance.service';
import { AttendanceComponent } from '../../teacher/schedule/attendance.component';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { DateTime } from 'luxon';
import { ChangeLogService } from '../../../services/change-log.service';
import { ChangeLog } from '../../../models/change-log.model';
import { Reason } from '../../../enums/reason.enum';
import { ClassScheduleService } from '../../../services/class-schedule.service';
import { AddStudentToClassComponent } from '../../teacher/schedule/add-student-to-class.component';
import { PopUpDialogComponent } from '../pop-up/pop-up-dialog.component';
import { CancelTutoringClassComponent } from '../../teacher/schedule/cancel-tutoring-class.component';

@Component({
  selector: 'app-class-details',
  templateUrl: './class-details.component.html',
  styleUrls: ['../../../styles/class-details.component.css']
})
export class ClassDetailsComponent implements OnInit{
  userId!: number;
  userRoles: Role[] = [];
  students: SimplifiedUser[] = [];
  jwtHelper = new JwtHelperService();
  changeLogs: ChangeLog[] = [];
  reason: string | undefined;
  currentUserIsStudent: boolean = false;
  currentUserIsTeacher: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<ClassDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClassSchedule,
    private dialog: MatDialog,
    private authService: AuthService,
    private tutoringClassService: TutoringClassService,
    private classScheduleService: ClassScheduleService,
    private attendanceService: AttendanceService,
    private changeLogService: ChangeLogService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    //Get current user
    this.loadUserData();
    this.currentUserIsStudent = this.authService.hasRole('STUDENT');
    this.currentUserIsTeacher = this.authService.hasRole('TEACHER');

    //Check if current user is teacher and this is his class
    if (this.currentUserIsTeacher && this.userId === this.data.tutoringClass.teacher?.id) {
      const classId = this.data.tutoringClass.id;
      if (classId !== undefined){
        this.loadStudents(classId);
      }
    }

    //Get changelog
    const classScheduleId = this.data.id!;
    this.loadChangeLog(classScheduleId); 
  }

  loadUserData() {
    const token = this.authService.getToken();
    if (token && !this.jwtHelper.isTokenExpired(token)) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      this.userId = decodedToken.id;
      this.userRoles = decodedToken.roles;
    }
  }

  loadStudents(classId: number) {
    this.tutoringClassService.getStudentsForClassSchedule(classId).subscribe({
      next: response => {
        this.students = response;
      },
      error: error => {
        console.error(error);
      }
    })
  }

  loadChangeLog(classId: number){
    this.changeLogService.getChangeLogForClassSchedule(classId).subscribe({
      next: response => {
        this.changeLogs = response.map(log => ({
          ...log,
          reason: Reason[log.reason as unknown as keyof typeof Reason]
        }));
      },
      error: error => {
        console.error(error);
      }
    })
  }

  openCancelSchedule(selectedClass: ClassSchedule): void {
    this.close();
    this.dialog.open(CancelScheduleComponent, {
      width: '50%',
      enterAnimationDuration:'200ms',
      exitAnimationDuration:'200ms',
      data: selectedClass,
    });
  }

  openEditClass(selectedClass: ClassSchedule): void {
    this.close();
    this.dialog.open(EditClassComponent, {
      width: '50%',
      enterAnimationDuration:'200ms',
      exitAnimationDuration:'200ms',
      data: selectedClass,
    });
  }

  openReserveRoom(scheduleId: number, classDateFrom: DateTime, classDateTo: DateTime): void {
    this.close();
    this.dialog.open(ReserveRoomComponent, {
      width: '50%',
      enterAnimationDuration:'200ms',
      exitAnimationDuration:'200ms',
      data: {
        scheduleId: scheduleId,
        classDateFrom: classDateFrom,
        classDateTo: classDateTo
      },
    });
  }

  openAttendance(scheduleId: number): void {
    this.attendanceService.getExistenceForClassSchedule(scheduleId).subscribe({
      next: (attendanceExists: boolean) => {
        if (!attendanceExists) {
          this.attendanceService.createAttendanceForClassSchedule(scheduleId).subscribe({
            next: () => {
              this.dialog.open(AttendanceComponent, {
                width: '50%',
                enterAnimationDuration: '200ms',
                exitAnimationDuration: '200ms',
                data: scheduleId,
              });
            },
            error: error => {
              console.error('Błąd podczas tworzenia obecności', error);
              this.toastr.error('Nie udało się utworzyć obecności');
            }
          });
        } else {
          this.dialog.open(AttendanceComponent, {
            width: '50%',
            enterAnimationDuration: '200ms',
            exitAnimationDuration: '200ms',
            data: scheduleId,
          });
        }
      },
      error: error => {
        console.error('Błąd podczas sprawdzania obecności', error);
        this.toastr.error('Nie udało się sprawdzić obecności');
      }
    });
  }

  openAddStudentToClass(){
    const classId = this.data.tutoringClass.id!;

    const dialogRef = this.dialog.open(AddStudentToClassComponent, {
      width: '30%',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms',
      data: { classId },
    })

    dialogRef.afterClosed().subscribe(() => {
      this.loadStudents(classId);
    });
  }

  openCancelClass(){
    const classId = this.data.tutoringClass.id!;

    const dialogRef = this.dialog.open(CancelTutoringClassComponent, {
      width: '30%',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms',
      data: { classId },
    })

    dialogRef.afterClosed().subscribe(() => {
      this.loadStudents(classId);
    });
  }

  completeClassSchedule(classId: number){
    this.classScheduleService.completeClassSchedule(classId).subscribe({
      next: response => {
        this.toastr.success("Zajęcia zostały oznaczone jako zakończone!","Sukces!")
        this.dialogRef.close(true)
      },
      error: error => {
        this.toastr.error("Coś poszło nie tak przy próbie zmiany stanu zajęć", "Błąd!")
        console.error("Coś poszło nie tak przy próbie zmiany stanu zajęć", error)
      }
    })
  }

  removeStudent(studentId: number){
    this.tutoringClassService.removeStudentFromTutoringClass(studentId, this.data.tutoringClass.id!).subscribe({
      next: response => {
        this.toastr.success(`Uczeń został usunięty z zajęć: ${this.data.tutoringClass.className}`,"Sukces!")
        this.dialogRef.close(true)
      },
      error: error => {
        if (error.error === "You cannot remove the only student from a class"){
          this.toastr.error("Nie można usunąć z zajęć jedynego ucznia, który na nie uczęszcza", "Błąd!")
        }else{
          this.toastr.error("Coś poszło nie tak przy próbie usunięcia ucznia z zajęć", "Błąd!")
          console.error("Coś poszło nie tak przy próbie zmiany stanu zajęć", error)
        }
      }
    })
  }

  confirmRemoveStudent(studentId: number): void {
    const dialogRef = this.dialog.open(PopUpDialogComponent, {
      width: '400px',
      data: {
        title: 'Potwierdzenie usunięcia',
        content: 'Czy na pewno chcesz usunąć tego ucznia z zajęć?',
        submitText: 'Usuń',
        cancelText: 'Anuluj'
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.removeStudent(studentId);
      }
    });
  }

  openProfile(userId: number){
    this.close();
    this.router.navigate([`/profile/${userId}`])
  }

  close(): void {
    this.dialogRef.close();
  }
}
