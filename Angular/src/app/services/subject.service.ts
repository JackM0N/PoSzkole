import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { Subject } from "../models/subject.model";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SubjectService {
  private baseUrl = environment.apiUrl + '/subject';

  constructor(private http: HttpClient){}

  loadSubjects(): Observable<Subject[]> {
    return this.http.get<Subject[]>(`${this.baseUrl}/all`)
  }

  loadCurrentTeacherSubjects(): Observable<Subject[]> {
    return this.http.get<Subject[]>(`${this.baseUrl}/current-teacher`)
  }

}