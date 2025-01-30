import { DayAndTime } from "./day-and-time.model";
import { TutoringClass } from "./tutoring-class.model";

export interface StudentRequestAndDate{
  studentId?: number;
  tutoringClassDTO?: TutoringClass;
  dayAndTimeDTO?: DayAndTime;
  repeatUntil?: [];
  isOnline?: boolean;
}