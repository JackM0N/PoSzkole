import { Component, Inject, OnInit } from '@angular/core';
import { Attendance } from '../../../models/attendance.model';
import { AttendanceService } from '../../../services/attendance.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-attendance',
  templateUrl: './attendance.component.html',
  styleUrl: '../../../styles/request-form.component.css'
})
export class AttendanceComponent implements OnInit{
  attendances: Attendance[] = [];

  constructor(
    public dialogRef: MatDialogRef<AttendanceComponent>,
    @Inject(MAT_DIALOG_DATA) public scheduleId: number,
    private attendanceService: AttendanceService,
    private toastr: ToastrService,
  ){}

  ngOnInit(): void {
    this.loadAttendance()
  }

  loadAttendance() {
    this.attendanceService.getAttendanceForClassSchedule(this.scheduleId).subscribe({
      next: response => {
        this.attendances = response;
      },
      error: error =>{
        console.error('Błąd podczas wczytywania obecności', error);
        this.toastr.error('Loading attendance error');
      }
    })
  }

  onSubmit() {
    this.attendanceService.checkAttendanceForClassSchedule(this.scheduleId, this.attendances).subscribe({
      next: () => {
        this.toastr.success('Obecność została zapisana!');
        this.dialogRef.close();
      },
      error: error => {
        console.error('Attendance update error', error);
        this.toastr.error('Nie udało się zapisać obecności');
      },
    });
  }

}
