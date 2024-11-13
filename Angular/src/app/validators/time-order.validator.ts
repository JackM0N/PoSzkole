import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const timeOrderValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => { 
  const timeFrom = group.get('timeFrom')?.value;
  const timeTo = group.get('timeTo')?.value;

  if (timeFrom && timeTo) {
    const timeFromDate = new Date(`1970-01-01T${timeFrom}:00`);
    const timeToDate = new Date(`1970-01-01T${timeTo}:00`);
    const timeDifference = (timeToDate.getTime() - timeFromDate.getTime()) / (1000 * 60 * 60);

    if (timeFromDate >= timeToDate) {
      return { timeOrderMismatch: true };
    } else if (timeDifference < 1) {
      return { minTimeDifference: true };
    }
  }
  return null;
};