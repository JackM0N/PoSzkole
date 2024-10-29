import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment.prod';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  login(userData: {username: string, password: string}): Observable<{ token: string}>{
    return this.http.post<{token: string}>(this.baseUrl + '/login', userData)
  }
}
