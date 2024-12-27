import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { CourseService } from '../../../services/course.service';
import { Course } from '../../../models/course.model';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../services/auth.service';
import { CourseAttendantsComponent } from '../../manager/courses/course-attendants.component';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrl: '../../../styles/course-details.component.css'
})
export class CourseDetailsComponent implements OnInit{
  courseDetails: string = '';
  currentUserIsManager: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<CourseDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
      course: Course;
    },
    private courseService: CourseService,
    private toastr: ToastrService,
    private authService: AuthService,
    private dialog: MatDialog,
  ){}

  ngOnInit(): void {
    this.loadCourseDetails();
    this.currentUserIsManager = this.authService.hasRole("MANAGER");
  }

  loadCourseDetails(){
    this.courseService.getCourseDescription(this.data.course.id!).subscribe({
      next: response => {
        this.courseDetails = response;
      },
      error: error => {
        this.toastr.error("Wystąpił błąd podczas wczytywania szcegółów kursu", "Błąd!")
        console.error("Error loading course details",error)
      }
    })
  }

  openAttendants(){
    this.dialogRef.close();
    this.dialog.open(CourseAttendantsComponent, {
      width: '50%',
      data: { courseId: this.data.course.id },
    });
  }
}
