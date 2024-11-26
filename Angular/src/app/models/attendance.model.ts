import { ClassSchedule } from "./class-schedule.model";
import { SimplifiedUser } from "./simplified-user.model";

export interface Attendance{
  id: number;
  classSchedule: ClassSchedule;
  student: SimplifiedUser;
  isPresent: boolean;
}