import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ScheduleService } from '../../../services/class-schedule.service';
import { ToastrService } from 'ngx-toastr';
import { Reason } from '../../../enums/reason.enum';
import { ScheduleChangesLog } from '../../../models/schedule-changes-log.model';

function getEnumKeyByValue<T extends object>(enumObject: T, value: string): string | undefined {
  return Object.keys(enumObject).find(key => enumObject[key as keyof T] === value);
}


@Component({
  selector: 'app-cancel-schedule',
  templateUrl: './cancel-schedule.component.html',
  styleUrls: ['../../../styles/request-form.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CancelScheduleComponent {

  scheduleChangeForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<CancelScheduleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClassSchedule,
    private fb: FormBuilder,
    private scheduleService: ScheduleService,
    private toastr: ToastrService) {
      this.scheduleChangeForm = this.fb.group({
        explanation: new FormControl('', {updateOn: 'blur'})
      });
    }


  onSubmit(scheduleId: number) {
    if (this.scheduleChangeForm.valid) {
      const scheduleChangeLog: ScheduleChangesLog = {
        reason: getEnumKeyByValue(Reason, Reason.STUDENT_REQUEST) as Reason,
        explanation: this.scheduleChangeForm.value.explanation,
      };
  
      this.scheduleService.cancelClassSchedule(scheduleId, scheduleChangeLog).subscribe({
        next: response => {
          this.toastr.success('Zajęcia zostały pomyślnie odwołane');
          this.dialogRef.close(true);
        },
        error: error => {
          console.error('Błąd podczas odwoływania zajęć', error);
          this.toastr.error('Nie udało się odwołać zajęć');
        }
      });
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}