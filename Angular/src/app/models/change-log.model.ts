import { Reason } from "../enums/reason.enum";
import { ClassSchedule } from "./class-schedule.model";
import { SimplifiedUser } from "./simplified-user.model";

export interface ChangeLog{
  id?: number;
  classSchedule?: ClassSchedule;
  user?: SimplifiedUser;
  reason?: Reason;
  explanation?: string;
}