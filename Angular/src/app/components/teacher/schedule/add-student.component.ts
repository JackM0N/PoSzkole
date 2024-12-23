import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { WebsiteUserService } from "../../../services/website-user.service";
import { Student } from "../../../models/student.model";
import { map, Observable, startWith } from "rxjs";
import { TutoringClassService } from "../../../services/tutoring-class.service";
import { ToastrService } from "ngx-toastr";

@Component({
  selector: 'app-add-student',
  templateUrl: './add-student.component.html',
  styleUrls: ['../../../styles/request-form.component.css']
})
export class AddStudentComponent implements OnInit{
  addStudentForm: FormGroup;
  students: Student[] = [];
  filteredStudents!: Observable<Student[]>;

  constructor(
    public dialogRef: MatDialogRef<AddStudentComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
      classId: number;
    },
    private fb: FormBuilder,
    private websiteUserService: WebsiteUserService,
    private tutoringClassService: TutoringClassService,
    private toastr: ToastrService,
  ){
    this.addStudentForm = this.fb.group({
      student: new FormControl('', [Validators.required])
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

  private filterStudents(value: string): Student[] {
    if (typeof value !== 'string') return [];
    const filterValue = value.toLowerCase();
    return this.students?.filter(student =>
      `${student.firstName} ${student.lastName}`.toLowerCase().includes(filterValue)
    ) || [];
  }

  displayStudent(student: Student): string {
    return student ? `${student.id} - ${student.firstName} ${student.lastName}` : '';
  }

  onSubmit() {
    if (this.addStudentForm.valid) {
      const selectedStudent = this.addStudentForm.get('student')!.value;

      console.log(this.data.classId)

      this.tutoringClassService.addStudentToTutoringClass(selectedStudent.id, this.data.classId).subscribe({
        next: response => {
          this.toastr.success("Pomyślnie dodano wybranego ucznia do zajęć");
          this.dialogRef.close(true);
        },
        error: error => {
          this.toastr.error("Coś poszło nie tak podczas próby dodania ucznia do zajęć");
          console.error('Something went wrong when adding student to class', error);
        }
      });
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}