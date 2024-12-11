import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Course } from "../models/course.model";

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private baseUrl = environment.apiUrl + '/course'

  constructor(private http: HttpClient){}

  getAvailableCourses(page: number, size: number, sortBy: string, sortDir: string): Observable<Course[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<Course[]>(`${this.baseUrl}/available-courses`, { params })
  }

  getBoughtCourses(page: number, size: number, sortBy: string, sortDir: string): Observable<Course[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<Course[]>(`${this.baseUrl}/bought-courses`, { params })
  }

  getActiveCourses(page: number, size: number, sortBy: string, sortDir: string): Observable<Course[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<Course[]>(`${this.baseUrl}/active-courses`, { params })
  }

}
