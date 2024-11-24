import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { CancelScheduleComponent } from '../../student/schedule/cancel-schedule.component';
import { JwtHelperService } from '@auth0/angular-jwt';
import { AuthService } from '../../../services/auth.service';
import { Role } from '../../../models/role.model';
import { TutoringClassService } from '../../../services/tutoring-class.service';
import { SimplifiedUser } from '../../../models/simplified-user.model';

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

  constructor(
    public dialogRef: MatDialogRef<ClassDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClassSchedule,
    private dialog: MatDialog,
    private authService: AuthService,
    private tutoringClassService: TutoringClassService,
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    if (this.hasRole('TEACHER') && this.userId === this.data.tutoringClass.teacher?.id) {
      const classId = this.data.tutoringClass.id;
      if (classId !== undefined){
        this.loadStudents(classId);
      }
    }
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
    this.tutoringClassService.getClassSchedulesForStudent(classId).subscribe({
      next: response => {
        this.students = response;
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

  hasRole(role: string): boolean {
    return this.userRoles.some(userRole => userRole.roleName === role);
  }

  close(): void {
    this.dialogRef.close();
  }
}
