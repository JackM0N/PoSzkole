import { SimplifiedUser } from "./simplified-user.model";
import { Subject } from "./subject.model";

export interface TutoringClass{
  id?: number;
  teacher?: SimplifiedUser;
  subject?: Subject;
  className?: string;
}