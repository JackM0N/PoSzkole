import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Attendance } from '../../../models/attendance.model';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { AttendanceService } from '../../../services/attendance.service';
import { Observer } from 'rxjs';
import { DateTime } from 'luxon';


@Component({
  selector: 'app-student-absence',
  templateUrl: './student-absence.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class StudentAbsenceComponent{
  protected dataSource: MatTableDataSource<Attendance> = new MatTableDataSource<Attendance>([]);
  protected totalAttendance: number = 0;
  protected displayedColumns: string[] = ['subjectName', 'classDateFrom', 'time', 'className'];
  protected noAttendance = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(private attendanceService: AttendanceService) {}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadPresentAttendance();

    this.sort.sortChange.subscribe(() => {
      this.loadPresentAttendance();
    });
  }

  protected columnSortMapping: { [key: string]: string } = {
    subjectName: 'classSchedule.tutoringClass.subject.subjectName',
    classDateFrom: 'classSchedule.classDateFrom',
    time: 'classSchedule.classDateFrom',
    className: 'classSchedule.tutoringClass.className'
  };

  loadPresentAttendance() {
    const page = this.paginator.pageIndex + 1;
    const size = this.paginator.pageSize || 10;
    const sortBy = this.columnSortMapping[this.sort.active] || 'classSchedule.classDateFrom';
    const sortDir = this.sort.direction || 'desc';
  
    const observer: Observer<any> = {
      next: response => {
        if (response) {
          response.content.forEach((attendance: Attendance) => {
             // Check if classDateFrom is an array and convert to DateTime
          if (Array.isArray(attendance.classSchedule?.classDateFrom)) {
            attendance.classSchedule.classDateFrom = this.convertArrayToDateTime(attendance.classSchedule.classDateFrom);
          }
           // Check if classDateTo is an array and convert to DateTime
          if (Array.isArray(attendance.classSchedule?.classDateTo)) {
            attendance.classSchedule.classDateTo = this.convertArrayToDateTime(attendance.classSchedule.classDateTo);
          }
        });
          this.totalAttendance = response.totalElements;
          this.dataSource = new MatTableDataSource<Attendance>(response.content);
          this.noAttendance = this.dataSource.data.length === 0;
        } else {
          this.noAttendance = true;
        }
      },
      error: error => {
        console.error(error);
      },
      complete: () => {}
    };
  
    this.attendanceService.getAttendancAbsent(page, size, sortBy, sortDir).subscribe(observer);
  }

  private convertArrayToDateTime(dateArray: number[]): DateTime {
    return DateTime.fromObject({
      year: dateArray[0],
      month: dateArray[1],
      day: dateArray[2],
      hour: dateArray[3],
      minute: dateArray[4]
    });
  }
}