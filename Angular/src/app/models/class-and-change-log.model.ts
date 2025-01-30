import { ClassSchedule } from "./class-schedule.model";
import { DateAndTime } from "./date-and-time.model";
import { ScheduleChangesLog } from "./schedule-changes-log.model";

export interface ClassAndChangeLog{
  classScheduleDTO: ClassSchedule;
  dateAndTimeDTO: DateAndTime;
  changeLogDTO: ScheduleChangesLog;
}