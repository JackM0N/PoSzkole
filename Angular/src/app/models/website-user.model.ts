import { PriceList } from "./price-list.model";
import { Role } from "./role.model";
import { Subject } from "./subject.model";

export interface WebsiteUser {
  id?: number;

  username?: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  gender?:string;
  email?: string;
  phone?: string;

  hourlyRate?: number;
  subjects?: Subject[];

  level?: string;
  guardianPhone?: string;
  guardianEmail?: string;
  priceList?: PriceList;
  discountProcentage?: number;
  isCashPayment?: boolean;
  issueInvoice?: boolean;

  roles?: Role[];
}