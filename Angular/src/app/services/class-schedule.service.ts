import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { RawClassSchedule } from "../models/raw-class-schedule.model";
import { ScheduleChangesLog } from "../models/schedule-changes-log.model";
import { ClassSchedule } from "../models/class-schedule.model";
import { ClassAndChangeLog } from "../models/class-and-change-log.model";

@Injectable({
  providedIn: 'root'
})
export class ClassScheduleService {
  private baseUrl = environment.apiUrl + '/schedule'

  constructor(private http: HttpClient){}
  
  getClassSchedulesForStudent(studentId: number): Observable<RawClassSchedule[]> {
    return this.http.get<RawClassSchedule[]>(`${this.baseUrl}/my-classes/student/${studentId}`);
  }

  getClassSchedulesForTeacher(teacherId: number): Observable<RawClassSchedule[]> {
    return this.http.get<RawClassSchedule[]>(`${this.baseUrl}/my-classes/teacher/${teacherId}`);
  }

  cancelClassSchedule(scheduleId: number, scheduleChangeLog: ScheduleChangesLog): Observable<ClassSchedule> {
    return this.http.put<ClassSchedule>(`${this.baseUrl}/cancel/${scheduleId}`, scheduleChangeLog)
  }

  updateClassSchedule(scheduleId: number, classAndChangeLog: ClassAndChangeLog) {
    return this.http.put<ClassSchedule>(`${this.baseUrl}/edit/${scheduleId}`, classAndChangeLog);
  }

  completeClassSchedule(scheduleId: number): Observable<ClassSchedule> {
    return this.http.put<ClassSchedule>(`${this.baseUrl}/complete/${scheduleId}`, {})
  }
}