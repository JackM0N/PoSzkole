import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { TutoringClass } from "../../../models/tutoring-class.model";
import { TutoringClassService } from "../../../services/tutoring-class.service";
import { ToastrService } from "ngx-toastr";
import { Subject } from "../../../models/subject.model";
import { DateTime } from "luxon";
import { RequestService } from "../../../services/request.service";

@Component({
  selector: 'app-admit-request-add',
  templateUrl: './admit-request-add.component.html',
  styleUrl: '../../../styles/reserve-room.component.css'
})
export class AdmitRequestAddComponent implements OnInit{
  tutoringClasses: TutoringClass[] = [];

  constructor(
    private ref: MatDialogRef<AdmitRequestAddComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
      subject: Subject;
      requestId: number;
    },
    private tutoringClassService: TutoringClassService,
    private requestService: RequestService,
    private toastr: ToastrService,
  ){}

  ngOnInit(): void {
    this.loadActiveClasses();
  }

  loadActiveClasses() {
    this.tutoringClassService.getTutoringClassesForTeacher(this.data.subject.id).subscribe({
      next: (response) => {
        this.tutoringClasses = response.map((tutoringClass) => {
          if (tutoringClass.nextClassSchedule?.classDateFrom) {
            const dateArray = tutoringClass.nextClassSchedule.classDateFrom; //Since dateFrom is an array
  
            if (Array.isArray(dateArray) && dateArray.length === 5) {
              //And since its an array, date has to be made fromObject
              const parsedDate = DateTime.fromObject({
                year: dateArray[0],
                month: dateArray[1],
                day: dateArray[2],
                hour: dateArray[3],
                minute: dateArray[4]
              });
              
              //Check if its valid
              if (parsedDate.isValid) {
                tutoringClass.nextClassSchedule.classDateFromFormatted = parsedDate.toFormat('dd.MM.yyyy, HH:mm');
              } else {
                console.error('Date parse error', dateArray);
              }
            }
          }
          return tutoringClass;
        });
      },
      error: (error) => {
        this.toastr.error('Nie można załadować aktywnych zajęć', 'Błąd');
        console.error('Loading active classes error', error);
      }
    });
  }
  
  addStudentToClass(classId: number){
    this.requestService.admitRequestAddToClass(this.data.requestId, classId).subscribe({
      next: response => {
        this.toastr.success("Dodano ucznia na wybrane zajęcia!", "Sukces!");
        this.ref.close(true);
      },
      error: error => {
        if (error.error === "You can't add this student to a class that he is already attending") {
          this.toastr.error("Nie można dodać ucznia na zajęcia, na które już uczęszcza.", "Błąd dodawania do zajęć");
        }else if (error.error === "You cannot add student to a class that's on students busy day") {
          this.toastr.error("Nie można dodać ucznia na te zajęcia, ponieważ jest on w ten dzień niedostępny.", "Błąd dodawania do zajęć");
        }else if (error.error === "Class schedule overlaps with existing class of this student") {
          this.toastr.error("Nie można dodać ucznia na te zajęcia, ponieważ nachodzą one na inne zajęcia, na które już uczęszcza.", "Błąd dodawania do zajęć");
        }else if (error.error === "This class does not have any more schedules") {
          this.toastr.error("Nie można dodać ucznia na te zajęcia, ponieważ wybrane zajęcia nie mają już terminów zajęć", "Błąd dodawania do zajęć");
        }else{
          this.toastr.error("Nie udało się dodać ucznia na zajęcia", "Błąd");
          console.error("Class update error", error);
        }
      }
    })
  }
}