import { Component, Inject, Input, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { CourseService } from "../../../services/course.service";
import { ToastrService } from "ngx-toastr";
import { Course } from "../../../models/course.model";
import { Editor } from "ngx-editor";

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.component.html',
  styleUrls: ['../../../styles/request-form.component.css'],
})
export class CourseFormComponent implements OnInit, OnDestroy{
  createCourseForm!: FormGroup;
  editor!: Editor;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<CourseFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      courseToEdit?: Course
    },
    private courseService: CourseService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.editor = new Editor();

    this.createCourseForm = this.fb.group({
      courseName: [this.data.courseToEdit?.courseName || '', [Validators.required, Validators.minLength(3)]],
      price: [this.data.courseToEdit?.price || '', [Validators.required, Validators.min(0)]],
      maxParticipants: [this.data.courseToEdit?.maxParticipants || '', [Validators.required, Validators.min(1)]],
      startDate: [this.parseDate(this.data.courseToEdit?.startDate), Validators.required],
      description: [this.data.courseToEdit?.description || '', Validators.required]
    });
  }

  ngOnDestroy(): void {
    this.editor.destroy();
  }

  onSubmit(): void {
    if (this.createCourseForm.valid) {
      const formData = this.createCourseForm.value;

      const newCourse: Course = {
        courseName: formData.courseName,
        price: formData.price,
        maxParticipants: formData.maxParticipants,
        startDate: formData.startDate.toISOString().split('T')[0],
        description: formData.description,
      };

      if (this.data.courseToEdit) {
        this.courseService.updateCourse(this.data.courseToEdit.id!, newCourse).subscribe({
          next: () => {
            this.toastr.success('Kurs został pomyślnie zaktualizowany!', 'Sukces!');
            this.dialogRef.close(true);
          },
          error: error => {
            this.toastr.error('Wystąpił błąd podczas edycji kursu.', 'Błąd');
            console.error(error);
          }
        });
      } else {
        this.courseService.createCourse(newCourse).subscribe({
          next: () => {
            this.toastr.success('Kurs został pomyślnie utworzony!', 'Sukces!');
            this.dialogRef.close(true);
          },
          error: error => {
            this.toastr.error('Wystąpił błąd podczas tworzenia kursu.', 'Błąd');
            console.error(error);
          }
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  private parseDate(startDate: number[] | undefined): Date | null {
    if (!startDate) {
      return null;
    }
    return new Date(startDate[0], startDate[1] - 1, startDate[2]);
  }
}