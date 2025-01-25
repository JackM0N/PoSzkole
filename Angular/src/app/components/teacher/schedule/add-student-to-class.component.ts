import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { WebsiteUserService } from "../../../services/website-user.service";
import { CompactUser } from "../../../models/compact-user.model";
import { map, Observable, startWith } from "rxjs";
import { TutoringClassService } from "../../../services/tutoring-class.service";
import { ToastrService } from "ngx-toastr";

@Component({
  selector: 'app-add-student-to-class',
  templateUrl: './add-student-to-class.component.html',
  styleUrls: ['../../../styles/request-form.component.css']
})
export class AddStudentToClassComponent implements OnInit{
  addStudentForm: FormGroup;
  students: CompactUser[] = [];
  filteredStudents!: Observable<CompactUser[]>;

  constructor(
    public dialogRef: MatDialogRef<AddStudentToClassComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
      classId: number;
    },
    private fb: FormBuilder,
    private websiteUserService: WebsiteUserService,
    private tutoringClassService: TutoringClassService,
    private toastr: ToastrService,
  ){
    this.addStudentForm = this.fb.group({
      student: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    this.loadStudents();

    this.filteredStudents = this.addStudentForm.get('student')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterStudents(value || ''))
    );
  }

  loadStudents() {
    this.websiteUserService.loadStudents().subscribe(data => {
      this.students = data;
    });
  }

  private filterStudents(value: string): CompactUser[] {
    if (typeof value !== 'string') return [];
    const filterValue = value.toLowerCase();
    return this.students?.filter(student =>
      `${student.firstName} ${student.lastName}`.toLowerCase().includes(filterValue)
    ) || [];
  }

  displayStudent(student: CompactUser): string {
    return student ? `${student.id} - ${student.firstName} ${student.lastName}` : '';
  }

  onSubmit() {
    if (this.addStudentForm.valid) {
      const selectedStudent = this.addStudentForm.get('student')!.value;

      this.tutoringClassService.addStudentToTutoringClass(selectedStudent.id, this.data.classId).subscribe({
        next: () => {
          this.toastr.success("Pomyślnie dodano wybranego ucznia do zajęć");
          this.dialogRef.close(true);
        },
        error: error => {
          if (error.error === "You can't add this student to a class that he is already attending") {
            this.toastr.error("Nie można dodać ucznia na zajęcia, na które już uczęszcza.", "Błąd dodawania do zajęć");
          }else if (error.error === "You cannot add student to a class that's on students busy day") {
            this.toastr.error("Nie można dodać ucznia na te zajęcia, ponieważ jest on w ten dzień niedostępny.", "Błąd dodawania do zajęć");
          }else if (error.error === "Class schedule overlaps with existing class of this student") {
            this.toastr.error("Nie można dodać ucznia na te zajęcia, ponieważ nachodzą one na inne zajęcia, na które już uczęszcza.", "Błąd dodawania do zajęć");
          }else{
            this.toastr.error("Coś poszło nie tak podczas próby dodania ucznia do zajęć");
            console.error('Adding student to class error', error);
          }
        }
      });
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}