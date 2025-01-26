import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { Reason } from '../../../enums/reason.enum';
import { DaysOfTheWeek } from '../../../enums/days-of-the-week.enum';
import { ClassAndChangeLog } from '../../../models/class-and-change-log.model';
import { ClassScheduleService } from '../../../services/class-schedule.service';
import { ToastrService } from 'ngx-toastr';
import { timeOrderValidator } from '../../../validators/time-order.validator';

function getEnumKeyByValue<T extends object>(enumObject: T, value: string): string | undefined {
  return Object.keys(enumObject).find(key => enumObject[key as keyof T] === value);
}

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
      dateAndTime: this.fb.group({
        date: [null],
        timeFrom: [null],
        timeTo: [null]
      }, {validators: timeOrderValidator}),
      scheduleChangesLog: this.fb.group({
        reason: [null, Validators.required],
        explanation: [null]
      })
    });
  }

  onSubmit(): void {
    if (!this.hasChanges()) {
      this.toastr.info('Nie wprowadzono żadnych zmian.');
      this.dialogRef.close(false);
      return;
    }

    const formData = this.editForm.value;

    const payload: ClassAndChangeLog = {
      classScheduleDTO: {
        ...formData.classSchedule,
        tutoringClass: {
          ...this.data.tutoringClass,
          className: formData.classSchedule.tutoringClass.className
        }
      },
      dateAndTimeDTO: formData.dateAndTime,
      changeLogDTO: {
        ...formData.scheduleChangesLog,
        reason: getEnumKeyByValue(Reason, formData.scheduleChangesLog.reason) as Reason
      }
    };

    this.classScheduleService.updateClassSchedule(payload.classScheduleDTO.id!, payload).subscribe({
      next: () => {
        this.toastr.success('Zajęcia zostały pomyślnie zaktualizowane.', 'Sukces!');
        this.dialogRef.close(true);
      },
      error: (error) => {
        if (error.error === "Class schedule overlaps with your schedule"){
          this.toastr.error('Wybrany termin zajęć koliduje z twoim harmonogramem.', 'Błąd!');
        } else if (error.error === "Class schedule overlaps with one of the student's class"){
          this.toastr.error('Wybrany termin zajęć koliduje z harmonogramem jednego z uczniów.', 'Błąd!');
        } else if (error.error === "Class schedule overlaps with busy day of one of the students"){
          this.toastr.error('Wybrany termin zajęć koliduje z dniami niedostępnymi jednego z uczniów.', 'Błąd!');
        } else if (error.error === "You must provide a reason for making changes in this class"){
          this.toastr.error('Musisz podać powód swoich zmian.', 'Błąd!');
        } else {
        console.error('Class update error',error);
        this.toastr.error('Nie udało się zaktualizować zajęć.', 'Błąd!');
        }
      }
    });
  }
  
  close(): void {
    this.dialogRef.close();
  }

  private hasChanges(): boolean {
    const formData = this.editForm.value;
  
    const initialData = {
      classSchedule: {
        id: this.data.id,
        tutoringClass: {
          className: this.data.tutoringClass?.className,
        },
        isOnline: this.data.isOnline,
        isCanceled: this.data.isCanceled,
      },
      dayAndTime: {
        day: null,
        timeFrom: null,
        timeTo: null,
      },
    };
  
    return JSON.stringify(formData.classSchedule) !== JSON.stringify(initialData.classSchedule) ||
           JSON.stringify(formData.dayAndTime) !== JSON.stringify(initialData.dayAndTime);
  }
}
