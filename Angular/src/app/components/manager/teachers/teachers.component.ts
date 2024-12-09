import { Component, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { SimplifiedUser } from '../../../models/simplified-user.model';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { WebsiteUserService } from '../../../services/website-user.service';
import { Observer } from 'rxjs';

@Component({
  selector: 'app-teachers',
  templateUrl: './teachers.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class TeachersComponent {
  protected dataSource: MatTableDataSource<SimplifiedUser> = new MatTableDataSource<SimplifiedUser>([]);
  protected totalTeachers: number = 0;
  protected displayedColumns: string[] = ['id', 'firstName', 'lastName', 'gender', 'email', 'phone', "subjects"];
  protected noTeachers = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(private websiteUserService: WebsiteUserService) {}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadTeachers();

    this.sort.sortChange.subscribe(() => {
      this.loadTeachers();
    });
  }

  loadTeachers() {
    const page = this.paginator.pageIndex + 1;
    const size = this.paginator.pageSize || 20;
    const sortBy = this.sort.active || 'id';
    const sortDir = this.sort.direction || 'asc';

    const observer: Observer<any> = {
      next: response => {
        if (response) {
          console.log(response)
          this.totalTeachers = response.totalElements;
          this.dataSource = new MatTableDataSource<SimplifiedUser>(response.content);
          this.noTeachers = this.dataSource.data.length === 0;
        } else {
          this.noTeachers = true;
        }
      },
      error: error => {
        console.error(error);
      },
      complete: () => {}
    };

    this.websiteUserService.loadAllTeachersPaged(page, size, sortBy, sortDir).subscribe(observer);
  }
}
