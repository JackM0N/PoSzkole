import { SimplifiedUser } from "./simplified-user.model";
import { CompactUser } from "./compact-user.model";
import { Subject } from "./subject.model";

export interface Request{
  id?: number;
  student?: CompactUser;
  subject?: Subject;
  repeatUntil?: [];
  prefersIndividual: boolean;
  prefersLocation: string;
  issueDate?: [];
  acceptanceDate?: [];
  teacher?: SimplifiedUser;
}