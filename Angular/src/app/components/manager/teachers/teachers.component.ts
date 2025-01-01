import { Component, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { SimplifiedUser } from '../../../models/simplified-user.model';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { WebsiteUserService } from '../../../services/website-user.service';
import { Observer } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { RegisterTeacherComponent } from './register-teacher.component';

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
  protected searchText: string = '';
  protected isDeleted: boolean = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(
    private websiteUserService: WebsiteUserService,
    private dialog: MatDialog
  ) {}

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

    this.websiteUserService.loadAllTeachersPaged(page, size, sortBy, sortDir, this.searchText, this.isDeleted).subscribe(observer);
  }

  openTeacherRegistration() {
    const dialogRef = this.dialog.open(RegisterTeacherComponent, {
      width: '30%'
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log("Add displaying newly added teacher")
      }
    });
  }

  onFilterChange() {
    this.paginator.pageIndex = 0;
    this.loadTeachers();
  }
}
