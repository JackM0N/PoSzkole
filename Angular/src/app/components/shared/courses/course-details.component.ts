import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CourseService } from '../../../services/course.service';
import { Course } from '../../../models/course.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrl: '../../../styles/course-details.component.css'
})
export class CourseDetailsComponent implements OnInit{
  courseDetails: string = '';

  constructor(
    public dialogRef: MatDialogRef<CourseDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
      course: Course;
    },
    private courseService: CourseService,
    private toastr: ToastrService
  ){}

  ngOnInit(): void {
    this.loadCourseDetails();
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
}
