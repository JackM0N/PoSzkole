import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { CompactUser } from "../models/compact-user.model";
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

  loadStudents(): Observable<CompactUser[]> {
    return this.http.get<CompactUser[]>(`${this.baseUrl}/all-students`)
  }

  loadTeachers(): Observable<CompactUser[]> {
    return this.http.get<CompactUser[]>(`${this.baseUrl}/all-teachers`)
  }

  loadAllStudentsPaged(page: number, size: number, sortBy: string, sortDir: string, searchText?: string, isDeleted?: boolean): Observable<SimplifiedUser[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );

    if (searchText) {
      params = params.set('searchText', searchText);
    }
  
    if (isDeleted !== undefined) {
      params = params.set('isDeleted', isDeleted.toString());
    }

    return this.http.get<SimplifiedUser[]>(`${this.baseUrl}/page/all-students`, {params})
  }

  loadAllTeachersPaged(page: number, size: number, sortBy: string, sortDir: string, searchText?: string, isDeleted?: boolean): Observable<SimplifiedUser[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );

    if (searchText) {
      params = params.set('searchText', searchText);
    }
  
    if (isDeleted !== undefined) {
      params = params.set('isDeleted', isDeleted.toString());
    }

    return this.http.get<SimplifiedUser[]>(`${this.baseUrl}/page/all-teachers`, {params})
  }

  editUserProfile(websiteUser: WebsiteUser): Observable<WebsiteUser> {
    return this.http.put<WebsiteUser>(`${this.baseUrl}/edit/my-profile`, websiteUser)
  }

  editTeacherSubjects(teacherId: number, subjects: Subject[]): Observable<WebsiteUser> {
    return this.http.put<WebsiteUser>(`${this.baseUrl}/edit/subjects/${teacherId}`, subjects)
  }

  deleteUser(userId: number): Observable<WebsiteUser> {
    return this.http.put<WebsiteUser>(`${this.baseUrl}/delete`, userId)
  }

  restoreUser(userId: number): Observable<WebsiteUser> {
    return this.http.put<WebsiteUser>(`${this.baseUrl}/restore`, userId)
  }
}