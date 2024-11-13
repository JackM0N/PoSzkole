import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RequestService } from '../../../services/request.service';
import { DaysOfTheWeek } from '../../../enums/days-of-the-week.enum';
import { RequestAdmit } from '../../../models/request-admit.model';
import { timeOrderValidator } from '../../../validators/time-order.validator';

@Component({
  selector: 'app-admit-request-pop-up',
  templateUrl: './admit-request-popup.component.html',
  styleUrl: '../../../styles/request-form.component.css'
})
export class AdmitRequestPopUpComponent implements OnInit{

  admitRequestForm: FormGroup;
  tutoringClassFormGroup!: FormGroup;
  days: DaysOfTheWeek[] = [];

  constructor(
    private ref: MatDialogRef<AdmitRequestPopUpComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
      requestId: number;
      studentId: number;
      studentName: string;
    },
    private fb: FormBuilder, 
    private requestService: RequestService) {
    this.admitRequestForm = this.fb.group({
      tutoringClassDTO: this.fb.group({
        className: new FormControl(''),
      }),
      dayAndTimeDTO: this.fb.group({
        day: new FormControl(''),
        timeFrom: new FormControl(''),
        timeTo: new FormControl(''),
      }, {validators: timeOrderValidator}),
      isOnline: new FormControl(false),
    });

    this.tutoringClassFormGroup = this.admitRequestForm.get('tutoringClassDTO') as FormGroup;
  }

  ngOnInit(): void {
    this.days = Object.values(DaysOfTheWeek);
  }

  onSubmit(requestId: number) {
    if (this.admitRequestForm.valid) {
      if (this.admitRequestForm.valid) {
        const formData = this.admitRequestForm.value;

        //Converting polish values back to their original values
        const dayKey = Object.keys(DaysOfTheWeek).find(
          key => DaysOfTheWeek[key as keyof typeof DaysOfTheWeek] === formData.dayAndTimeDTO.day
        );

        const requestAdmit: RequestAdmit = {
          tutoringClassDTO: this.admitRequestForm.get('tutoringClassDTO')?.value,
          dayAndTimeDTO: {
            day: dayKey || formData.dayAndTimeDTO.day,
            timeFrom: formData.dayAndTimeDTO.timeFrom + ':00',
            timeTo: formData.dayAndTimeDTO.timeTo + ':00'
          },
          isOnline: this.admitRequestForm.get('isOnline')?.value
        };

        this.requestService.admitRequest(requestId, requestAdmit).subscribe({
          next: () => {
            //TODO: Add some sort of refresh after admiting request
            this.closePopup();
          },
          error: (error) => {
            console.error("Error admitting request:", error);
          }
        });
      }
    }
  }

  get dayAndTimeFormGroup() {
    return this.admitRequestForm.get('dayAndTimeDTO') as FormGroup;
  }

  closePopup(){
    this.ref.close();
  }
}
