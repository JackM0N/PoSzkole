import { Reason } from "../enums/reason.enum";

export interface ScheduleChangesLog{
  id?: number;
  reason: Reason;
  explanation?: string;
}