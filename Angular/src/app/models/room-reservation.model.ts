import { Room } from "./room.model";
import { SimplifiedUser } from "./simplified-user.model";

export interface RoomReservation {
  id: number;
  room: Room;
  teacher: SimplifiedUser;
  reservationFrom: string;
  reservationTo: string;
}