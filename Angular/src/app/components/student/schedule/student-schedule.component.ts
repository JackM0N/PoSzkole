import { Component, OnInit } from "@angular/core";
import { ClassSchedule } from "../../../models/class-schedule.model";
import { ScheduleService } from "../../../services/class-schedule.service";
import { DateTime } from "luxon";
import { RawClassSchedule } from "../../../models/raw-class-schedule.model";

@Component({
  selector: 'app-student-schedule',
  templateUrl: './student-schedule.component.html',
  styleUrl: '../../../styles/schedule.component.css'
})
export class StudentScheduleComponent implements OnInit{
  classes: ClassSchedule[] = [];

  constructor(private scheduleService: ScheduleService){}

  ngOnInit(): void {
    this.fetchClassSchedules();
  }

  fetchClassSchedules(): void {
    this.scheduleService.getClassSchedulesForStudent().subscribe({
      next: (data: RawClassSchedule[]) => {
        this.classes = data.map((classSchedule) => ({
          ...classSchedule,
          classDateFrom: DateTime.fromObject({
            year: classSchedule.classDateFrom[0],
            month: classSchedule.classDateFrom[1],
            day: classSchedule.classDateFrom[2],
            hour: classSchedule.classDateFrom[3],
            minute: classSchedule.classDateFrom[4],
          }),
          classDateTo: DateTime.fromObject({
            year: classSchedule.classDateTo[0],
            month: classSchedule.classDateTo[1],
            day: classSchedule.classDateTo[2],
            hour: classSchedule.classDateTo[3],
            minute: classSchedule.classDateTo[4],
          }),
        }));
      },
      error: (err) => {
        console.error('Błąd podczas pobierania harmonogramu:', err);
      },
    });
  }
}