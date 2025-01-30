import { Room } from "./room.model";
import { TutoringClass } from "./tutoring-class.model";

export interface RawClassSchedule {
  id?: number;
  tutoringClass: TutoringClass;
  room: Room;
  classDateFrom: number[];
  classDateTo: number[];
  isOnline: boolean;
  isCompleted: boolean;
  isCanceled: boolean;
}