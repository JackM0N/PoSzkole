import { DayAndTime } from "./day-and-time.model";
import { TutoringClass } from "./tutoring-class.model";

export interface RequestAdmit{
  id?: number;
  tutoringClassDTO: TutoringClass;
  dayAndTimeDTO: DayAndTime;
  isOnline: boolean;
}