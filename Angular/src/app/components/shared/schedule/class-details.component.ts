import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { CancelScheduleComponent } from '../../student/schedule/cancel-schedule.component';

@Component({
  selector: 'app-class-details',
  templateUrl: './class-details.component.html',
  styleUrls: ['../../../styles/class-details.component.css']
})
export class ClassDetailsComponent {
  //TODO: Add actual class info
  constructor(
    public dialogRef: MatDialogRef<ClassDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClassSchedule,
    private dialog: MatDialog,
  ) {}

  openCancelSchedule(selectedClass: ClassSchedule): void {
    this.dialog.open(CancelScheduleComponent, {
      width: '50%',
      enterAnimationDuration:'200ms',
      exitAnimationDuration:'200ms',
      data: selectedClass,
    });
  }

  close(): void {
    this.dialogRef.close();
  }
}
