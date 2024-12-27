import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { UserBusyDay } from '../../../models/user-busy-day.model';
import { timeOrderValidator } from '../../../validators/time-order.validator';
import { UserBusyDayService } from '../../../services/user-busy-day.service';
import { DaysOfTheWeek } from '../../../enums/days-of-the-week.enum';

@Component({
  selector: 'app-edit-busy-days',
  templateUrl: './edit-busy-days.component.html',
  styleUrl: '../../../styles/edit-busy-days.component.css'
})
export class EditBusyDaysComponent {
  busyDayForm!: FormGroup;
  days: DaysOfTheWeek[] = [];

  constructor(
    private fb: FormBuilder,
    private toastr: ToastrService,
    private dialogRef: MatDialogRef<EditBusyDaysComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      isCreation: boolean, 
      busyDay?: UserBusyDay
    },
    private userBusyDayService: UserBusyDayService
  ){
    this.days = Object.values(DaysOfTheWeek);

    this.busyDayForm = this.fb.group({
      dayOfTheWeek: [
        this.data.busyDay?.dayOfTheWeek 
          ? DaysOfTheWeek[this.data.busyDay.dayOfTheWeek as unknown as keyof typeof DaysOfTheWeek]
          : null
      ],
      timeFrom: [this.data.busyDay?.timeFrom || null],
      timeTo: [this.data.busyDay?.timeTo || null]
    }, {validators: timeOrderValidator});
  }

  onSubmit() {
    if (this.busyDayForm.invalid) {
      this.toastr.error('Formularz zawiera błędy.', 'Błąd');
      return;
    }

    const formValue = this.busyDayForm.value as UserBusyDay;
    const busyDay: UserBusyDay = {
      ...this.data.busyDay,
      ...formValue,
      dayOfTheWeek: Object.keys(DaysOfTheWeek).find(
        key => DaysOfTheWeek[key as keyof typeof DaysOfTheWeek] === formValue.dayOfTheWeek
      ) as DaysOfTheWeek
    };

    if (this.data.isCreation) {
      this.createBusyDay(busyDay);
    } else {
      this.editBusyDay(busyDay);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

  private createBusyDay(busyDay: UserBusyDay): void {
    this.userBusyDayService.createUserBusyDay(busyDay).subscribe({
      next: () => {
        this.toastr.success('Zajęty dzień został pomyślnie utworzony.', 'Sukces');
        this.dialogRef.close(true);
      },
      error: () => {
        this.toastr.error('Wystąpił błąd podczas tworzenia zajętego dnia.', 'Błąd');
      }
    });
  }

  private editBusyDay(busyDay: UserBusyDay): void {
    this.userBusyDayService.editUserBusyDay(busyDay).subscribe({
      next: () => {
        this.toastr.success('Zajęty dzień został pomyślnie zaktualizowany.', 'Sukces');
        this.dialogRef.close(true);
      },
      error: () => {
        this.toastr.error('Wystąpił błąd podczas edycji zajętego dnia.', 'Błąd');
      }
    });
  }

  onDelete() {
    this.userBusyDayService.deleteUserBusyDay(this.data.busyDay!.id!).subscribe({
      next: () => {
        this.toastr.success('Zajęty dzień został pomyślnie zaktualizowany.', 'Sukces');
        this.dialogRef.close(true);
      },
      error: () => {
        this.toastr.error('Wystąpił błąd podczas edycji zajętego dnia.', 'Błąd');
      }
    });
  }
}
