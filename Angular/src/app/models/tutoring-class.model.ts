import { Subject } from "./subject.model";
import { WebsiteUser } from "./website-user.model";

export interface TutoringClass{
  id?: number;
  teacher?: WebsiteUser;
  subject?: Subject;
  className?: string;
}