import { Student } from "./student.model";
import { Subject } from "./subject.model";
import { WebsiteUser } from "./website-user.model";

export interface Request{
  id?: number;
  student?: Student;
  subject?: Subject;
  repeatUntil?: [];
  prefersIndividual: boolean;
  prefersLocation: string;
  issueDate?: [];
  acceptanceDate?: [];
  teacher?: WebsiteUser; //TODO: Change to simplified teacher
}