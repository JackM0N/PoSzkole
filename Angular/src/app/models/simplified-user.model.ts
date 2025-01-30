
import { Role } from "./role.model";
import { Subject } from "./subject.model";

export interface SimplifiedUser {
  id?: number;

  firstName?: string;
  lastName?: string;
  gender?:string;
  email?: string;
  phone?: string;

  level?: string;
  guardianPhone?: string;
  guardianEmail?: string;

  subjects?: Subject[];

  role?: Role;
}