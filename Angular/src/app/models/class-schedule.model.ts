import { DateTime } from "luxon";
import { Room } from "./room.model";
import { TutoringClass } from "./tutoring-class.model";

export interface ClassSchedule {
  id?: number;
  tutoringClass: TutoringClass;
  room: Room;
  classDateFrom: DateTime;
  classDateTo: DateTime;
  isOnline: boolean;
  isCompleted: boolean;
  isCanceled: boolean;

  classDateFromFormatted?: string;
}