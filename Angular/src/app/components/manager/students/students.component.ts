import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { SimplifiedUser } from '../../../models/simplified-user.model';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { WebsiteUserService } from '../../../services/website-user.service';
import { Observer } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { RegisterStudentComponent } from './register-student.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class StudentsComponent implements AfterViewInit{
  protected dataSource: MatTableDataSource<SimplifiedUser> = new MatTableDataSource<SimplifiedUser>([]);
  protected totalStudents: number = 0;
  protected displayedColumns: string[] = ['id', 'firstName', 'lastName', 'gender', 'email', 'phone', "guardianEmail", "guardianPhone"];
  protected noStudents = false;
  protected searchText: string = '';
  protected isDeleted: boolean = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(
    private websiteUserService: WebsiteUserService,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadStudents();

    this.sort.sortChange.subscribe(() => {
      this.loadStudents();
    });
  }

  loadStudents() {
    const page = this.paginator.pageIndex + 1;
    const size = this.paginator.pageSize || 20;
    const sortBy = this.sort.active || 'id';
    const sortDir = this.sort.direction || 'asc';

    const observer: Observer<any> = {
      next: response => {
        if (response) {
          this.totalStudents = response.totalElements;
          this.dataSource = new MatTableDataSource<SimplifiedUser>(response.content);
          this.noStudents = this.dataSource.data.length === 0;
        } else {
          this.noStudents = true;
        }
      },
      error: error => {
        console.error(error);
      },
      complete: () => {}
    };

    this.websiteUserService.loadAllStudentsPaged(page, size, sortBy, sortDir, this.searchText, this.isDeleted).subscribe(observer);
  }

  openStudentRegistration() {
    const dialogRef = this.dialog.open(RegisterStudentComponent, {
      width: '30%'
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.router.navigate([`/profile/${result}`])
      }
    });
  }

  onFilterChange() {
    this.paginator.pageIndex = 0;
    this.loadStudents();
  }
}
