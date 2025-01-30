import { DaysOfTheWeek } from "../enums/days-of-the-week.enum";
import { SimplifiedUser } from "./simplified-user.model";

export interface RawUserBusyDay{
  id?: number;
  user?: SimplifiedUser;
  dayOfTheWeek?: DaysOfTheWeek;
  timeFrom?: number[];
  timeTo?: number[];
}