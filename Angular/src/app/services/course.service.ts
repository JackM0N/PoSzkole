import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Course } from "../models/course.model";
import { SimplifiedUser } from "../models/simplified-user.model";
import { StartCourse } from "../models/startCourse.model";

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private baseUrl = environment.apiUrl + '/course'

  constructor(private http: HttpClient){}

  getNotStartedCourses(page: number, size: number, sortBy: string, sortDir: string): Observable<Course[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<Course[]>(`${this.baseUrl}/not-started-courses`, { params })
  }

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

  getCourseDescription(courseId: number): Observable<string>{
    return this.http.get<string>(`${this.baseUrl}/description/${courseId}`, { responseType: 'text' as 'json' })
  }

  getCourseAttendants(courseId: number): Observable<SimplifiedUser[]>{
    return this.http.get<SimplifiedUser[]>(`${this.baseUrl}/attendants/${courseId}`)
  }

  createCourse(course: Course): Observable<Course>{
    return this.http.post<Course>(`${this.baseUrl}/create`, course)
  }

  startCourse(startCourse: StartCourse): Observable<Course>{
    return this.http.post<Course>(`${this.baseUrl}/start-course`, startCourse)
  }

  addStudentToCourse(studentId: number, courseId: number): Observable<Course>{
    return this.http.put<Course>(`${this.baseUrl}/add-student`,{
      studentId: studentId,
      courseId: courseId
    })
  }

  removeStudentToCourse(studentId: number, courseId: number): Observable<Course>{
    return this.http.put<Course>(`${this.baseUrl}/remove-student`,{
      studentId: studentId,
      courseId: courseId
    })
  }

  openCourseForRegistration(courseId: number): Observable<Course>{
    return this.http.put<Course>(`${this.baseUrl}/open/${courseId}`, {})
  }

  finishCourse(courseId: number): Observable<Course>{
    return this.http.put<Course>(`${this.baseUrl}/finish/${courseId}`, {})
  }

  updateCourse(courseId: number, course: Course): Observable<Course>{
    return this.http.put<Course>(`${this.baseUrl}/edit/${courseId}`, course)
  }

}
