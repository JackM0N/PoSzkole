import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SimplifiedUser } from '../../../models/simplified-user.model';
import { CourseService } from '../../../services/course.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-course-attendants',
  templateUrl: './course-attendants.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class CourseAttendantsComponent implements OnInit {
  students: SimplifiedUser[] = [];
  displayedColumns: string[] = ['student', 'email', 'phone', 'guardianEmail', 'guardianPhone'];

  constructor(
    public dialogRef: MatDialogRef<CourseAttendantsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { courseId: number },
    private courseService: CourseService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.courseService.getCourseAttendants(this.data.courseId).subscribe((students) => {
      this.students = students;
    });
  }

  navigateToStudentProfile(studentId: number): void {
    this.dialogRef.close();
    this.router.navigate(['/profile/', studentId]);
  }
}
