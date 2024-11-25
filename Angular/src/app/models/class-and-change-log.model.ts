import { ClassSchedule } from "./class-schedule.model";
import { DayAndTime } from "./day-and-time.model";
import { ScheduleChangesLog } from "./schedule-changes-log.model";

export interface ClassAndChangeLog{
  classSchedule: ClassSchedule;
  dayAndTime: DayAndTime;
  scheduleChangesLog: ScheduleChangesLog;
}