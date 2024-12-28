import { DayAndTime } from "./day-and-time.model";
import { TutoringClass } from "./tutoring-class.model";

export interface StartCourse{
  courseId: number;
  tutoringClassDTO: TutoringClass;
  teacherId: number;
  dayAndTimeDTO: DayAndTime;
  isOnline: boolean;
  repeatUntil: [];
}