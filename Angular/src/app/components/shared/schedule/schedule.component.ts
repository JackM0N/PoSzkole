import { Component, computed, input, InputSignal, signal, Signal, WritableSignal } from "@angular/core";
import { DateTime, Info, Interval } from "luxon";
import { ClassSchedule } from "../../../models/class-schedule.model";

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrl: '../../../styles/schedule.component.css',
})
export class ScheduleComponent {
  classes: InputSignal<ClassSchedule[]> = input.required();
  today: Signal<DateTime> = signal(DateTime.local());
  firstDayOfActiveMonth: WritableSignal<DateTime> = signal(
    this.today().startOf('month')
  );
  activeDay: WritableSignal<DateTime | null> = signal(null);
  weekDays: Signal<string[]> = signal(Info.weekdays('short'))
  daysOfMonth: Signal<DateTime[]> = computed(() =>{
    return Interval.fromDateTimes(
      this.firstDayOfActiveMonth().startOf('week'),
      this.firstDayOfActiveMonth().endOf('month').endOf('week')
    )
    .splitBy({day: 1})
    .map((d) => {
      if(d.start === null) {
        throw new Error('Złe daty');
      }
      return d.start;
    });
  });
  DATE_MED = DateTime.DATE_MED;
  activeDayClasses: Signal<ClassSchedule[]> = computed(() => {
    const activeDay = this.activeDay();
    if (activeDay === null) {
      return [];
    }
    const activeDayISO = activeDay.toISODate();
  
    if (!activeDayISO) {
      return [];
    }
  
    return this.classes().filter((schedule) => {
      const scheduleDateISO = DateTime.fromISO(schedule.classDateFrom as unknown as string).toISODate();
      return scheduleDateISO === activeDayISO;
    });
  });

  constructor(){}

  goToPreviousMonth(): void{
    this.firstDayOfActiveMonth.set(
      this.firstDayOfActiveMonth().minus({month: 1})
    )
  }

  goToNextMonth(): void{
    this.firstDayOfActiveMonth.set(
      this.firstDayOfActiveMonth().plus({month: 1})
    )
  }

  goToToday(): void{
    this.firstDayOfActiveMonth.set(
      this.today().startOf('month')
    )
  }
}