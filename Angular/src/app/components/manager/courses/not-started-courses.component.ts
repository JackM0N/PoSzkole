import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Course } from '../../../models/course.model';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { CourseService } from '../../../services/course.service';
import { Observer } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { CourseDetailsComponent } from '../../shared/courses/course-details.component';
import { StartCourseComponent } from './start-course.component';
import { CourseFormComponent } from './course-form.component';
import { AddStudentToCourseComponent } from './add-student-to-course.component';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-not-started-courses',
  templateUrl: './not-started-courses.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class NotStartedCoursesComponent implements AfterViewInit{
  protected dataSource: MatTableDataSource<Course> = new MatTableDataSource<Course>([]);
  protected totalCourses: number = 0;
  protected displayedColumns: string[] = ['courseName', 'startDate', 'price', 'maxParticipants', 'isOpenForRegistration', 'action'];
  protected noCourses = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(
    private courseService: CourseService,
    private dialog: MatDialog,
    private toastr: ToastrService,
  ) {}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadNotStartedCourses();

    this.sort.sortChange.subscribe(() => {
      this.loadNotStartedCourses();
    });
  }

  loadNotStartedCourses() {
    const page = this.paginator.pageIndex + 1;
    const size = this.paginator.pageSize || 10;
    const sortBy = this.sort.active || 'courseName';
    const sortDir = this.sort.direction || 'asc';

    const observer: Observer<any> = {
      next: response => {
        if (response) {
          this.totalCourses = response.totalElements;
          this.dataSource = new MatTableDataSource<Course>(response.content);
          this.noCourses = this.dataSource.data.length === 0;
        } else {
          this.noCourses = true;
        }
      },
      error: error => {
        console.error(error);
      },
      complete: () => {}
    };

    this.courseService.getNotStartedCourses(page, size, sortBy, sortDir).subscribe(observer);
  }

  openDetails(course: Course){
    const dialogRef = this.dialog.open(CourseDetailsComponent, {
      width: '35%',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms',
      data: { course },
    })

    dialogRef.afterClosed().subscribe(() => {
      this.loadNotStartedCourses();
    });
  }

  openStartCourse(course: Course){
    const dialogRef = this.dialog.open(StartCourseComponent, {
      width: '35%',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms',
      data: { course },
    })

    dialogRef.afterClosed().subscribe(() => {
      this.loadNotStartedCourses();
    });
  }

  openCourseForm(course?: Course){
    const dialogRef = this.dialog.open(CourseFormComponent, {
      width: '35%',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms',
      data: { courseToEdit: course }
    })

    dialogRef.afterClosed().subscribe(() => {
      this.loadNotStartedCourses();
    });
  }

  openAddStudentToCourse(course: Course){
    const dialogRef = this.dialog.open(AddStudentToCourseComponent, {
      width: '35%',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms',
      data: { course },
    })

    dialogRef.afterClosed().subscribe(() => {
      this.loadNotStartedCourses();
    });
  }

  openForRegistration(courseId: number){
    this.courseService.openCourseForRegistration(courseId).subscribe({
      next: () => {
        this.toastr.success("Wybrany kurs został otworzony na rejestracje!","Sukces!")
        this.loadNotStartedCourses();
      },
      error: error => {
        this.toastr.error("Coś poszło nie tak przy otwieraniu kursu", "Błąd!")
        console.error("Course update error", error)
      }
    })
  }

  deleteCourse(courseId: number){
    this.courseService.deleteCourse(courseId).subscribe({
      next: () => {
        this.toastr.success("Wybrany kurs został usunięty!","Sukces!")
        this.loadNotStartedCourses();
      },
      error: error => {
        this.toastr.error("Coś poszło nie tak przy usuwaniu kursu", "Błąd!")
        console.error("Coś poszło nie tak przy usuwaniu kursu", error)
      }
    })
  }
}
