import { DayAndTime } from "./day-and-time.model";
import { TutoringClass } from "./tutoring-class.model";

export interface StartCourse{
  id?: number;
  tutoringClass: TutoringClass;
  teacherId: number;
  dayAndTime: DayAndTime;
  isOnline: boolean;
  repeatUntil: [];
}