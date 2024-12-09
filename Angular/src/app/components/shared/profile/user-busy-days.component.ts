import { Component, Input } from '@angular/core';
import { UserBusyDay } from '../../../models/user-busy-day.model';
import { DaysOfTheWeek } from '../../../enums/days-of-the-week.enum';
import { UserBusyDayService } from '../../../services/user-busy-day.service';

@Component({
  selector: 'app-user-busy-days',
  templateUrl: './user-busy-days.component.html',
  styleUrl: '../../../styles/user-busy-days.component.css'
})
export class UserBusyDaysComponent {
  @Input() userId: number | undefined;

  busyDays: UserBusyDay[] = [];

  daysOfWeek: { name: string; value: string }[] = [];

  busyIntervalsByDay: { [key: string]: UserBusyDay[] } = {};

  constructor(private userBusyDayService: UserBusyDayService){}

  ngOnInit(): void {
    if (this.userId) {
      this.getUserBusyDays();
      this.initDaysOfWeek();
    }
  }

  getUserBusyDays() {
    this.userBusyDayService.getUserBusyDays(this.userId!).subscribe({
      next: response => {
        this.busyDays = response;
        this.organizeBusyDays();
      },
      error: error => {
        console.error("Wystąpił problem z wczytaniem twojego profilu", error);
      }
    })
  }

  openEditBusyDay() {
    console.log("Here") 
  }

  initDaysOfWeek() {
    // Enum to object conversion
    this.daysOfWeek = Object.keys(DaysOfTheWeek).map(key => ({
      value: key,
      name: DaysOfTheWeek[key as keyof typeof DaysOfTheWeek].slice(0,3)
    }));
  }

  organizeBusyDays() {
    this.daysOfWeek.forEach(day => {
      this.busyIntervalsByDay[day.value] = this.busyDays.filter(busyDay => busyDay.dayOfTheWeek === day.value);
    });
  }
}
