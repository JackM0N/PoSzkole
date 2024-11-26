import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Room } from '../models/room.model';
import { RoomReservation } from '../models/room-reservation.model';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class RoomReservationService {
  private baseUrl = environment.apiUrl + '/room-reservation';

  constructor(private http: HttpClient) {}


  getRoomsForSchedule(classScheduleId: number): Observable<Room[]> {
    return this.http.get<Room[]>(`${this.baseUrl}/list/${classScheduleId}`);
  }

  reserveRoom(roomId: number, classScheduleId: number): Observable<RoomReservation> {
    return this.http.post<RoomReservation>(`${this.baseUrl}/reserve/${roomId}`, classScheduleId);
  }
}
