import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { SimplifiedUser } from "../models/simplified-user.model";
import { TutoringClass } from "../models/tutoring-class.model";

@Injectable({
  providedIn: 'root'
})
export class TutoringClassService {
  private baseUrl = environment.apiUrl + '/class'

  constructor(private http: HttpClient){}
  
  getStudentsForClassSchedule(classId: number): Observable<SimplifiedUser[]> {
    return this.http.get<SimplifiedUser[]>(`${this.baseUrl}/student-list/${classId}`);
  }

  getTutoringClassesForTeacher(subjectId: number): Observable<TutoringClass[]> {
    return this.http.get<TutoringClass[]>(`${this.baseUrl}/active-classes/subject/${subjectId}`)
  }

  addStudentToTutoringClass(studentId: number, classId: number): Observable<TutoringClass> {
    return this.http.post<TutoringClass>(`${this.baseUrl}/add-student`, {
      studentId: studentId,
      classId: classId
    });
  }
}