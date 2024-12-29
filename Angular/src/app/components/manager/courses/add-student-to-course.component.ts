import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { CompactUser } from "../../../models/compact-user.model";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { WebsiteUserService } from "../../../services/website-user.service";
import { CourseService } from "../../../services/course.service";
import { ToastrService } from "ngx-toastr";
import { map, Observable, startWith } from "rxjs";
import { Course } from "../../../models/course.model";

@Component({
  selector: 'app-add-student-to-course',
  templateUrl: './add-student-to-course.component.html',
  styleUrls: ['../../../styles/request-form.component.css']
})
export class AddStudentToCourseComponent implements OnInit{
  addStudentForm: FormGroup;
  students: CompactUser[] = [];
  filteredStudents!: Observable<CompactUser[]>;

  constructor(
    public dialogRef: MatDialogRef<AddStudentToCourseComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
      course: Course;
    },
    private fb: FormBuilder,
    private websiteUserService: WebsiteUserService,
    private courseService: CourseService,
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

      this.courseService.addStudentToCourse(selectedStudent.id, this.data.course.id!).subscribe({
        next: () => {
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