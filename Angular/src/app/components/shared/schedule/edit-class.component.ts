import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { Reason } from '../../../enums/reason.enum';
import { DaysOfTheWeek } from '../../../enums/days-of-the-week.enum';
import { ClassAndChangeLog } from '../../../models/class-and-change-log.model';
import { ClassScheduleService } from '../../../services/class-schedule.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-edit-class',
  templateUrl: './edit-class.component.html',
  styleUrls: ['../../../styles/request-form.component.css']
})
export class EditClassComponent {
  editForm!: FormGroup;
  reasons = Object.values(Reason);
  days = Object.values(DaysOfTheWeek);

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EditClassComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClassSchedule,
    private classScheduleService: ClassScheduleService,
    private toastr: ToastrService,
  ) {}

  ngOnInit(): void{
    this.initForm();
  }

  private initForm(): void {
    this.editForm = this.fb.group({
      classSchedule: this.fb.group({
        id: [this.data.id],
        tutoringClass: this.fb.group({
          className: [this.data.tutoringClass?.className, Validators.required]
        }),
        isOnline: [this.data.isOnline],
        isCanceled: [this.data.isCanceled]
      }),
      dayAndTime: this.fb.group({
        day: [null],
        timeFrom: [null],
        timeTo: [null]
      }),
      scheduleChangesLog: this.fb.group({
        reason: [null, Validators.required],
        explanation: [null]
      })
    });
  }

  onSubmit(): void {
    const formData = this.editForm.value;

    const payload: ClassAndChangeLog = {
      classSchedule: {
        ...formData.classSchedule,
        tutoringClass: {
          ...this.data.tutoringClass,
          className: formData.classSchedule.tutoringClass.className
        }
      },
      dayAndTime: formData.dayAndTime,
      scheduleChangesLog: formData.scheduleChangesLog
    };

    this.classScheduleService.updateClassSchedule(payload.classSchedule.id!, payload).subscribe({
      next: () => {
        this.toastr.success('Zajęcia zostały pomyślnie zaktualizowane.');
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Nie udało się zaktualizować zajęć.');
      }
    });
  }

  
  close(): void {
    this.dialogRef.close();
  }
}
