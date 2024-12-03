import { SimplifiedUser } from "./simplified-user.model";
import { Student } from "./student.model";
import { Subject } from "./subject.model";

export interface Request{
  id?: number;
  student?: Student;
  subject?: Subject;
  repeatUntil?: [];
  prefersIndividual: boolean;
  prefersLocation: string;
  issueDate?: [];
  acceptanceDate?: [];
  teacher?: SimplifiedUser;
}