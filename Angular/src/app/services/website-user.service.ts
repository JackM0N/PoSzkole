import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { Student } from "../models/student.model";
import { Observable } from "rxjs";
import { HttpClient, HttpParams } from "@angular/common/http";
import { WebsiteUser } from "../models/website-user.model";
import { Subject } from "../models/subject.model";
import { SimplifiedUser } from "../models/simplified-user.model";

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

  loadAllStudentsPaged(page: number, size: number, sortBy: string, sortDir: string): Observable<SimplifiedUser[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<SimplifiedUser[]>(`${this.baseUrl}/page/all-students`, {params})
  }

  loadAllTeachersPaged(page: number, size: number, sortBy: string, sortDir: string): Observable<SimplifiedUser[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<SimplifiedUser[]>(`${this.baseUrl}/page/all-teachers`, {params})
  }

  editUserProfile(websiteUser: WebsiteUser): Observable<WebsiteUser> {
    return this.http.put<WebsiteUser>(`${this.baseUrl}/edit/my-profile`, websiteUser)
  }

  editTeacherSubjects(teacherId: number, subjects: Subject[]): Observable<WebsiteUser> {
    return this.http.put<WebsiteUser>(`${this.baseUrl}/edit/subjects/${teacherId}`, subjects)
  }

}