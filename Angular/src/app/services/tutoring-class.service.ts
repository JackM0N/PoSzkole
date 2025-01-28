import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { SimplifiedUser } from "../models/simplified-user.model";
import { TutoringClass } from "../models/tutoring-class.model";
import { ScheduleChangesLog } from "../models/schedule-changes-log.model";
import { StudentRequestAndDate } from "../models/student-request-and-date.model";

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

  createTutoringClass(srd: StudentRequestAndDate): Observable<TutoringClass> {
    return this.http.post<TutoringClass>(`${this.baseUrl}/create`, srd);
  }

  removeStudentFromTutoringClass(studentId: number, classId: number): Observable<TutoringClass> {
    return this.http.put<TutoringClass>(`${this.baseUrl}/remove-student`, {
      studentId: studentId,
      classId: classId
    });
  }

  cancelTheRestOfTutoringClass(classId: number, scheduleChangeLog: ScheduleChangesLog): Observable<TutoringClass> {
    return this.http.put<TutoringClass>(`${this.baseUrl}/cancel/${classId}`, scheduleChangeLog);
  }
}