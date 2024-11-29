import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { Student } from "../models/student.model";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { WebsiteUser } from "../models/website-user.model";
import { Subject } from "../models/subject.model";

@Injectable({
  providedIn: 'root'
})
export class WebsiteUserService {
  private baseUrl = environment.apiUrl + '/user';

  constructor(private http: HttpClient){}

  loadCurrentUserProfile(): Observable<WebsiteUser> {
    return this.http.get<WebsiteUser>(`${this.baseUrl}/my-profile`)
  }

  loadUserProfile(userId: number): Observable<WebsiteUser> {
    return this.http.get<WebsiteUser>(`${this.baseUrl}/profile/${userId}`)
  }

  loadStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.baseUrl}/all-students`)
  }

  editUserProfile(websiteUser: WebsiteUser): Observable<WebsiteUser> {
    return this.http.put<WebsiteUser>(`${this.baseUrl}/edit/my-profile`, websiteUser)
  }

  editTeacherSubjects(teacherId: number, subjects: Subject[]): Observable<WebsiteUser> {
    return this.http.put<WebsiteUser>(`${this.baseUrl}/edit/subjects/${teacherId}`, subjects)
  }

}