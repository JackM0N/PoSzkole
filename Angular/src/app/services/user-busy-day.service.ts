import { Injectable } from "@angular/core";
import { environment } from "../../environment/environment";
import { map, Observable } from "rxjs";
import { UserBusyDay } from "../models/user-busy-day.model";
import { HttpClient } from "@angular/common/http";
import { RawUserBusyDay } from "../models/raw-user-busy-day.model";
import { DateTime } from "luxon";

@Injectable({
  providedIn: 'root'
})
export class UserBusyDayService {
  private baseUrl = environment.apiUrl + '/busy-days';

  constructor(private http: HttpClient){}

  getUserBusyDays(userId: number): Observable<UserBusyDay[]> {
    return this.http.get<RawUserBusyDay[]>(`${this.baseUrl}/list/${userId}`).pipe(
      map(rawBusyDays =>
        rawBusyDays.map(rawBusyDay => ({
          id: rawBusyDay.id,
          user: rawBusyDay.user,
          dayOfTheWeek: rawBusyDay.dayOfTheWeek,
          timeFrom: this.convertToTimeString(rawBusyDay.timeFrom),
          timeTo: this.convertToTimeString(rawBusyDay.timeTo),
        }))
      )
    );;
  }

  private convertToTimeString(timeArray?: number[]): string | null {
    if (!timeArray || timeArray.length < 2) {
      return null;
    }
    const [hours, minutes] = timeArray;

    const time = DateTime.fromObject({ hour: hours, minute: minutes });

    return time.toFormat('HH:mm');
  }
}