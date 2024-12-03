import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Course } from '../../../models/course.model';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { CourseService } from '../../../services/course.service';
import { Observer } from 'rxjs';

@Component({
  selector: 'app-student-courses',
  templateUrl: './student-courses.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class StudentCoursesComponent implements AfterViewInit{
  protected dataSource: MatTableDataSource<Course> = new MatTableDataSource<Course>([]);
  protected totalCourses: number = 0;
  protected displayedColumns: string[] = ['courseName', 'startDate', 'price', 'maxParticipants', 'action'];
  protected noCourses = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(private courseService: CourseService) {}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadBoughtCourses();

    this.sort.sortChange.subscribe(() => {
      this.loadBoughtCourses();
    });
  }

  loadBoughtCourses() {
    const page = this.paginator.pageIndex + 1;
    const size = this.paginator.pageSize || 10;
    const sortBy = this.sort.active || 'courseName';
    const sortDir = this.sort.direction || 'asc';

    const observer: Observer<any> = {
      next: response => {
        if (response) {
          console.log(response)
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

    this.courseService.getBoughtCourses(page, size, sortBy, sortDir).subscribe(observer);
  }
}
