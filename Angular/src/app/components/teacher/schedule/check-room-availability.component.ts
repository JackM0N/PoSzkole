import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DateTime } from 'luxon';

@Component({
  selector: 'app-check-room-availability',
  templateUrl: './check-room-availability.component.html',
  styleUrl: '../../../styles/reserve-room.component.css'
})
export class CheckRoomAvailiabilityComponent{
  activeDay: DateTime;
  timeForm: FormGroup;
  showRooms: boolean = false;
  reserveRoomData: any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { activeDay: string },
    private fb: FormBuilder,
  ) {
    this.activeDay = DateTime.fromISO(data.activeDay);
    this.timeForm = this.fb.group({
      timeFrom: ['', Validators.required],
      timeTo: ['', Validators.required]
    });
  }

  checkAvailability(): void {
    const { timeFrom, timeTo } = this.timeForm.value;

    // Combine activeDate and chosen times
    const classDateFrom = DateTime.fromISO(this.data.activeDay)
      .set({ hour: +timeFrom.split(':')[0], minute: +timeFrom.split(':')[1] });
    const classDateTo = DateTime.fromISO(this.data.activeDay)
      .set({ hour: +timeTo.split(':')[0], minute: +timeTo.split(':')[1] });

    // Check if dates are in correct order
    if (classDateTo <= classDateFrom) {
      alert('Godzina zakończenia musi być późniejsza od godziny rozpoczęcia');
      return;
    }

    this.reserveRoomData = {
      id: null,
      classDateFrom,
      classDateTo
    };

    this.showRooms = true;
  }

}