import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { Observable } from "rxjs";
import { ChangeLog } from "../models/change-log.model";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ChangeLogService {
  private baseUrl = environment.apiUrl + '/changelog';

  constructor(private http: HttpClient){}

  getChangeLogForClassSchedule(classScheduleId: number): Observable<ChangeLog[]>{
    return this.http.get<ChangeLog[]>(`${this.baseUrl}/class-schedule/${classScheduleId}`)
  }
}