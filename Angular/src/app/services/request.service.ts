import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { Request } from "../models/request.model";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class RequestService {
  private baseUrl = environment.apiUrl + '/request'

  constructor(private http: HttpClient){}

  createRequest(requestForm: Request): Observable<any>{
    return this.http.post<any>(`${this.baseUrl}/create`, requestForm)
  }
}