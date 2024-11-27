import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ClassSchedule } from "../models/class-schedule.model";
import { Attendance } from "../models/attendance.model";

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private baseUrl = environment.apiUrl + '/attendance'

  constructor(private http: HttpClient){}
  
  getAttendanceForClassSchedule(scheduleId: number): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(`${this.baseUrl}/list/${scheduleId}`);
  }

  getExistenceForClassSchedule(scheduleId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/exists/${scheduleId}`);
  }

  createAttendanceForClassSchedule(scheduleId: number): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/create/${scheduleId}`, null);
  }

  checkAttendanceForClassSchedule(scheduleId: number, attendances: Attendance[]) {
    return this.http.put<ClassSchedule>(`${this.baseUrl}/check/${scheduleId}`, attendances);
  }
}