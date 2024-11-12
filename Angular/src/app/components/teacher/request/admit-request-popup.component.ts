import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RequestService } from '../../../services/request.service';
import { DaysOfTheWeek } from '../../../enums/days-of-the-week.enum';

@Component({
  selector: 'app-admit-request-pop-up',
  templateUrl: './admit-request-popup.component.html',
  styleUrl: '../../../styles/request-form.component.css'
})
export class AdmitRequestPopUpComponent implements OnInit{

  admitRequestForm: FormGroup;
  days: DaysOfTheWeek[] = [];

  constructor(
    private ref: MatDialogRef<AdmitRequestPopUpComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
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
      }),
      isOnline: new FormControl(false),
    });
  }

  ngOnInit(): void {
    this.days = Object.values(DaysOfTheWeek);
  }

  onSubmit() {
    if (this.admitRequestForm.valid) {
      // Logika obsługi przyjęcia requestu, np. wysłanie danych do serwisu
      console.log(this.admitRequestForm.value);
      this.closePopup();
    }
  }

  closePopup(){
    this.ref.close();
  }
}
