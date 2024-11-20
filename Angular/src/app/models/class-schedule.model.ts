import { DateTime } from "luxon";
import { Room } from "./room.model";

export interface ClassSchedule {
  id?: number;
  tutoringClassName: string;
  room: Room;
  classDateFrom: DateTime;
  classDateTo: DateTime;
  isOnline: boolean;
  isCompleted: boolean;
  isCanceled: boolean;
}