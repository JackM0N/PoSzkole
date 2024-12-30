import { Component, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Course } from '../../../models/course.model';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { CourseService } from '../../../services/course.service';
import { Observer } from 'rxjs';
import { CourseDetailsComponent } from '../../shared/courses/course-details.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-manager-courses',
  templateUrl: './manager-courses.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class ManagerCoursesComponent {
  protected dataSource: MatTableDataSource<Course> = new MatTableDataSource<Course>([]);
  protected totalCourses: number = 0;
  protected displayedColumns: string[] = ['courseName', 'startDate', 'price', 'maxParticipants', 'action'];
  protected noCourses = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(
    private courseService: CourseService,
    private dialog: MatDialog,
  ) {}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadActiveCourses();

    this.sort.sortChange.subscribe(() => {
      this.loadActiveCourses();
    });
  }

  loadActiveCourses() {
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

    this.courseService.getActiveCourses(page, size, sortBy, sortDir).subscribe(observer);
  }

  openDetails(course: Course){
      const dialogRef = this.dialog.open(CourseDetailsComponent, {
        width: '35%',
        enterAnimationDuration: '200ms',
        exitAnimationDuration: '200ms',
        data: { course },
      })
  
      dialogRef.afterClosed().subscribe(() => {
        this.loadActiveCourses();
      });
    }
}
