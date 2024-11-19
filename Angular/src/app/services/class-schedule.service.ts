import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { RawClassSchedule } from "../models/raw-class-schedule.model";

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private baseUrl = environment.apiUrl + '/schedule'

  constructor(private http: HttpClient){}
  
  getClassSchedulesForStudent(): Observable<RawClassSchedule[]> {
    return this.http.get<RawClassSchedule[]>(`${this.baseUrl}/my-classes`);
  }
}