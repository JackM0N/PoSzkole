import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { Student } from "../models/student.model";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class WebsiteUserService {
  private baseUrl = environment.apiUrl + '/user';

  constructor(private http: HttpClient){}

  loadStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.baseUrl}/all-students`)
  }

}