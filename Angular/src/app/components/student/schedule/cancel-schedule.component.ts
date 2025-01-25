import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ClassScheduleService } from '../../../services/class-schedule.service';
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
    private scheduleService: ClassScheduleService,
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
        next: () => {
          this.toastr.success('Zajęcia zostały pomyślnie odwołane');
          this.dialogRef.close(true);
        },
        error: error => {
          if(error.error === "You can only cancel individual classes"){
            this.toastr.error("Nie możesz odwołać zajęć na które uczęszczają inni uczniowie. W razie potrzeby skontaktuj się z nauczycielem!", "Błąd")
          }
          if(error.error === "You cannot cancel a class that starts in less than 24 hours"){
            this.toastr.error("Nie możesz odwołać zajęć, które mają się zacząć za mniej niż 24 godziny", "Błąd")
          }
          else{
            console.error('Class update error', error);
            this.toastr.error('Nie udało się odwołać zajęć');
          }
        }
      });
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}