import { Room } from "./room.model";

export interface RawClassSchedule {
  id?: number;
  tutoringClassName: string;
  room: Room;
  classDateFrom: number[];
  classDateTo: number[];
  isOnline: boolean;
  isCompleted: boolean;
  isCanceled: boolean;
}