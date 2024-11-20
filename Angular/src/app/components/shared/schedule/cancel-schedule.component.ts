import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { ClassDetailsComponent } from './class-details.component';

@Component({
  selector: 'app-cancel-schedule',
  templateUrl: './cancel-schedule.component.html',
  styleUrls: ['../../../styles/request-form.component.css']
})
export class CancelScheduleComponent {
  constructor(
    public dialogRef: MatDialogRef<ClassDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClassSchedule
  ) {}

  close(): void {
    this.dialogRef.close();
  }
}