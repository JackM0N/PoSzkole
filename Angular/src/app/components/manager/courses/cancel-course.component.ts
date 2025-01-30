import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Reason } from "../../../enums/reason.enum";
import { EditClassComponent } from "../../shared/schedule/edit-class.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { Course } from "../../../models/course.model";
import { CourseService } from "../../../services/course.service";
import { ToastrService } from "ngx-toastr";
import { ChangeLog } from "../../../models/change-log.model";

function getEnumKeyByValue<T extends object>(enumObject: T, value: string): string | undefined {
  return Object.keys(enumObject).find(key => enumObject[key as keyof T] === value);
}

@Component({
  selector: 'app-cancel-course',
  templateUrl: './cancel-course.component.html',
  styleUrls: ['../../../styles/request-form.component.css']
})
export class CancelCourseComponent implements OnInit{
  cancelForm!: FormGroup;
  reasons = Object.values(Reason);

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EditClassComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      course: Course;
    },
    private courseService: CourseService,
    private toastr: ToastrService,
  ){}

  ngOnInit(): void {
    this.cancelForm = this.fb.group({
      reason: ['', Validators.required],
      explanation: [null]
    })
  }

  onSubmit(): void {
    if(this.cancelForm.valid){
      const formData = this.cancelForm.value;

      const changeLog: ChangeLog = {
        reason: getEnumKeyByValue(Reason, formData.reason) as Reason,
        explanation: formData.explanation
      };

      this.courseService.cancelCourse(this.data.course.id!, changeLog).subscribe({
        next: () => {
          this.toastr.success('Kurs został pomyślnie odwołany!', 'Sukces!');
          this.dialogRef.close(true);
        },
        error: error => {
          this.toastr.error('Wystąpił błąd podczas odwoływania kursu.', 'Błąd');
          console.error('Course cancelation error',error);
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}