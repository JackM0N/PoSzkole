import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";
import { CourseService } from "../../../services/course.service";
import { ToastrService } from "ngx-toastr";
import { Course } from "../../../models/course.model";
import { Editor } from "ngx-editor";

@Component({
  selector: 'app-create-course',
  templateUrl: './create-course.component.html',
  styleUrls: ['../../../styles/request-form.component.css'],
})
export class CreateCourseComponent implements OnInit, OnDestroy{
  createCourseForm!: FormGroup;
  editor!: Editor;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<CreateCourseComponent>,
    private courseService: CourseService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.editor = new Editor();

    this.createCourseForm = this.fb.group({
      courseName: ['', [Validators.required, Validators.minLength(3)]],
      price: ['', [Validators.required, Validators.min(0)]],
      maxParticipants: ['', [Validators.required, Validators.min(1)]],
      startDate: [null, Validators.required],
      description: ['', Validators.required]
    });
  }

  ngOnDestroy(): void {
    this.editor.destroy();
  }

  onSubmit(): void {
    if (this.createCourseForm.valid) {
      const formData = this.createCourseForm.value;

      console.log(formData)

      const newCourse: Course = {
        courseName: formData.courseName,
        price: formData.price,
        maxParticipants: formData.maxParticipants,
        startDate: formData.startDate.toISOString().split('T')[0],
        description: formData.description,
      };

      console.log(newCourse)

      this.courseService.createCourse(newCourse).subscribe({
        next: () => {
          this.toastr.success('Kurs został pomyślnie utworzony!', 'Sukces!');
          this.dialogRef.close(true);
        },
        error: error => {
          this.toastr.error('Wystąpił błąd podczas tworzenia kursu.', 'Błąd');
          console.error(error)
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}