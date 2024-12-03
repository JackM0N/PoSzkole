import { SimplifiedUser } from "./simplified-user.model";

export interface Course{
  id?: number;
  courseName?: string;
  price?: number;
  maxParticipants?: number;
  startDate?: number[];
  isOpenForRegistration?: boolean;
  isDone?: boolean;
  students?: SimplifiedUser[]
}