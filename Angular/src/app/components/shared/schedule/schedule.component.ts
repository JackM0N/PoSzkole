import { AfterViewInit, Component, computed, ElementRef, Input, input, InputSignal, OnInit, Renderer2, signal, Signal, WritableSignal } from "@angular/core";
import { DateTime, Info, Interval } from "luxon";
import { ClassSchedule } from "../../../models/class-schedule.model";
import { MatDialog } from "@angular/material/dialog";
import { ClassDetailsComponent } from "./class-details.component";

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrl: '../../../styles/schedule.component.css',
})
export class ScheduleComponent implements OnInit, AfterViewInit{
  @Input() isReadOnly: boolean = false;
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

  constructor(
    private dialog: MatDialog,
    private el: ElementRef, 
    private renderer: Renderer2
  ){}

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    setTimeout(() => {
      const observer = new ResizeObserver((entries) => {
        for (const entry of entries) {
          const width = this.el.nativeElement.offsetWidth;
  
          if (width < 900) {
            this.renderer.addClass(this.el.nativeElement, 'small-container');
          } else {
            this.renderer.removeClass(this.el.nativeElement, 'small-container');
          }
        }
      });
  
      observer.observe(this.el.nativeElement);
    }, 0);
  }
  

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

  hasClassesForDay(day: DateTime): boolean {
    return this.classes().some((classSchedule) =>
      day.hasSame(DateTime.fromISO(classSchedule.classDateFrom as unknown as string), 'day')
    );
  }

  trackByFn(index: number, item: ClassSchedule): number | undefined {
    return item.id;
  }

  openDetailsDialog(selectedClass: ClassSchedule): void {
    this.dialog.open(ClassDetailsComponent, {
      width: '50%',
      enterAnimationDuration:'200ms',
      exitAnimationDuration:'200ms',
      data: selectedClass,
    });
  }
}