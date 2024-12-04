import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { ClassSchedule } from "../models/class-schedule.model";
import { Attendance } from "../models/attendance.model";

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private baseUrl = environment.apiUrl + '/attendance'

  constructor(private http: HttpClient){}

  getAttendancePresent(page: number, size: number, sortBy: string, sortDir: string): Observable<Attendance[]> {
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<Attendance[]>(`${this.baseUrl}/presence`, { params })
  }

  getAttendancAbsent(page: number, size: number, sortBy: string, sortDir: string): Observable<Attendance[]> {
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<Attendance[]>(`${this.baseUrl}/absence`, { params })
  }
  
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