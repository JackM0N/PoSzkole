import { Component, Input, OnInit } from "@angular/core";
import { ClassSchedule } from "../../../models/class-schedule.model";
import { ClassScheduleService } from "../../../services/class-schedule.service";
import { DateTime } from "luxon";
import { RawClassSchedule } from "../../../models/raw-class-schedule.model";
import { JwtHelperService } from "@auth0/angular-jwt";
import { AuthService } from "../../../services/auth.service";

@Component({
  selector: 'app-teacher-schedule',
  templateUrl: './teacher-schedule.component.html',
  styleUrl: '../../../styles/schedule.component.css'
})
export class TeacherScheduleComponent implements OnInit{
  @Input() userId: number | null = null;
  classes: ClassSchedule[] = [];
  isReadOnly: boolean = false;
  jwtHelper = new JwtHelperService();

  constructor(private scheduleService: ClassScheduleService, private authService: AuthService){}

  ngOnInit(): void {
    if (!this.userId) {
      const token = this.authService.getToken();
      if (token && !this.jwtHelper.isTokenExpired(token)) {
        const decodedToken = this.jwtHelper.decodeToken(token);
        this.userId = decodedToken.id;
        this.fetchClassSchedules();
      }
    } else {
      this.isReadOnly = true;
      this.fetchClassSchedules();
    }
  }

  fetchClassSchedules(): void {
    this.scheduleService.getClassSchedulesForTeacher(this.userId!).subscribe({
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
      error: (error) => {
        console.error('Loading schedule error', error);
      },
    });
  }
}