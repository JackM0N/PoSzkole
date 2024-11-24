import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { RawClassSchedule } from "../models/raw-class-schedule.model";
import { ScheduleChangesLog } from "../models/schedule-changes-log.model";
import { ClassSchedule } from "../models/class-schedule.model";

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private baseUrl = environment.apiUrl + '/schedule'

  constructor(private http: HttpClient){}
  
  getClassSchedulesForStudent(): Observable<RawClassSchedule[]> {
    return this.http.get<RawClassSchedule[]>(`${this.baseUrl}/my-classes/student`);
  }

  getClassSchedulesForTeacher(): Observable<RawClassSchedule[]> {
    return this.http.get<RawClassSchedule[]>(`${this.baseUrl}/my-classes/teacher`);
  }

  cancelClassSchedule(scheduleId: number, scheduleChangeLog: ScheduleChangesLog): Observable<ClassSchedule> {
    return this.http.put<ClassSchedule>(`${this.baseUrl}/cancel/${scheduleId}`, scheduleChangeLog)
  }
}