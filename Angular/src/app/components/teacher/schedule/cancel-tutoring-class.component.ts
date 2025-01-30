import { Component, Inject } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { TutoringClassService } from "../../../services/tutoring-class.service";
import { ToastrService } from "ngx-toastr";
import { ScheduleChangesLog } from "../../../models/schedule-changes-log.model";
import { Reason } from "../../../enums/reason.enum";

function getEnumKeyByValue<T extends object>(enumObject: T, value: string): string | undefined {
  return Object.keys(enumObject).find(key => enumObject[key as keyof T] === value);
}

@Component({
  selector: 'app-cancel-tutoring-class',
  templateUrl: './cancel-tutoring-class.component.html',
  styleUrls: ['../../../styles/request-form.component.css'],
})
export class CancelTutoringClassComponent {

  scheduleChangeForm: FormGroup;
  reasons = Object.values(Reason);

  constructor(
    public dialogRef: MatDialogRef<CancelTutoringClassComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      classId: number
    },
    private fb: FormBuilder,
    private tutoringClassService: TutoringClassService,
    private toastr: ToastrService) {
      this.scheduleChangeForm = this.fb.group({
        reason: new FormControl(null, Validators.required),
        explanation: new FormControl('', Validators.required)
      });
    }
  
  onSubmit() {
    if (this.scheduleChangeForm.valid) {
      const scheduleChangeLog: ScheduleChangesLog = {
        reason: getEnumKeyByValue(Reason, this.scheduleChangeForm.value.reason) as Reason,
        explanation: this.scheduleChangeForm.value.explanation,
      };

      this.tutoringClassService.cancelTheRestOfTutoringClass(this.data.classId, scheduleChangeLog).subscribe({
        next: () => {
          this.toastr.success('Wszystkie pozostałe terminy zajęć zostały pomyślnie odwołane.', 'Sukces!');
          this.dialogRef.close(true);
        },
        error: (error) => {
          console.error('Class update error', error);
          this.toastr.error('Nie udało się odwołać zajęć.', 'Błąd!');
        }
      })
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
