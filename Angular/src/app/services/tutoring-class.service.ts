import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { SimplifiedUser } from "../models/simplified-user.model";

@Injectable({
  providedIn: 'root'
})
export class TutoringClassService {
  private baseUrl = environment.apiUrl + '/class'

  constructor(private http: HttpClient){}
  
  getClassSchedulesForStudent(classId: number): Observable<SimplifiedUser[]> {
    return this.http.get<SimplifiedUser[]>(`${this.baseUrl}/student-list/${classId}`);
  }
}