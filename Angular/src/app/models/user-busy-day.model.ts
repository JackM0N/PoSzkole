import { DaysOfTheWeek } from "../enums/days-of-the-week.enum";
import { SimplifiedUser } from "./simplified-user.model";

export interface UserBusyDay{
  id?: number;
  user?: SimplifiedUser;
  dayOfTheWeek?: DaysOfTheWeek;
  timeFrom?: string | null;
  timeTo?: string | null;
}