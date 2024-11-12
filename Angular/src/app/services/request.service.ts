import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { Request } from "../models/request.model";
import { Observable } from "rxjs";
import { HttpClient, HttpParams } from "@angular/common/http";
import { RequestAdmit } from "../models/request-admit.model";

@Injectable({
  providedIn: 'root'
})
export class RequestService {
  private baseUrl = environment.apiUrl + '/request'

  constructor(private http: HttpClient){}

  createRequest(requestForm: Request): Observable<any>{
    return this.http.post<any>(`${this.baseUrl}/create`, requestForm)
  }

  getNotAdmittedRequests(page: number, size: number, sortBy: string, sortDir: string): Observable<Request[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<Request[]>(`${this.baseUrl}/list/not-admitted`, { params })
  }

  getAdmittedRequests(page: number, size: number, sortBy: string, sortDir: string): Observable<Request[]>{
    var params = new HttpParams()
      .set('page', (page - 1).toString())
      .set('size', size.toString())
      .set('sort', sortBy + ',' + sortDir
    );
    return this.http.get<Request[]>(`${this.baseUrl}/list/admitted`, { params })
  }

  admitRequest(requestId: number, requestForm: RequestAdmit): Observable<Request>{
    return this.http.post<Request>(`${this.baseUrl}/admit/${requestId}`, requestForm)
  }
}