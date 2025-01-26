import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Request } from '../../../models/request.model';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { RequestService } from '../../../services/request.service';
import { Observer } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { AdmitRequestCreateComponent } from './admit-request-create.component';
import { Router } from '@angular/router';
import { AdmitRequestAddComponent } from './admit-request-add.component';
import { Subject } from '../../../models/subject.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-request-list',
  templateUrl: './request-list.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class RequestListComponent implements AfterViewInit{
  protected dataSource: MatTableDataSource<Request> = new MatTableDataSource<Request>([]);
  protected totalRequests: number = 0;
  protected UnadmittedDisplayedColumns: string[] = ['student', 'subject.subjectName', 'repeatUntil', 'prefersIndividual', 'prefersLocation', 'issueDate', 'action'];
  protected noRequests = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(
    private requestService: RequestService, 
    private dialog: MatDialog,
    private router: Router,
    private toastr: ToastrService){}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadNotAdmittedRequests();

    this.sort.sortChange.subscribe(() => {
      this.loadNotAdmittedRequests();
    });
  }

  loadNotAdmittedRequests(){
    const page = this.paginator.pageIndex + 1;
    const size = this.paginator.pageSize;
    const sortBy = this.sort.active || 'issueDate';
    const sortDir = this.sort.direction || 'asc';

    const observer: Observer<any> = {
      next: response => {
        if (response) {
          this.totalRequests = response.totalElements;
          this.dataSource = new MatTableDataSource<Request>(response.content);
          this.noRequests = (this.dataSource.data.length == 0);
        }else{
          this.noRequests = true;
        }
      },
      error: error => {
        if(error.error === "Teacher has no subjects assigned"){
          this.toastr.info("Nie zadeklarowałeś/aś jakich przedmiotów nauczasz. Ustaw przedmioty, których nauczasz w swoim profilu!", "Informacja")
        }
        console.error("Loading requests error", error)
      },
      complete: () => {}
    };
    this.requestService.getNotAdmittedRequests(page, size, sortBy, sortDir).subscribe(observer);
  }

  openCreateClass(requestId: number, studentId: number, studentName: string){
    const dialogRef = this.dialog.open(AdmitRequestCreateComponent,{
      width:'50%',
      enterAnimationDuration:'200ms',
      exitAnimationDuration:'200ms',
      data:{
        requestId: requestId,
        studentId: studentId,
        studentName: studentName
      }
    });

    dialogRef.afterClosed().subscribe(() => {
      this.loadNotAdmittedRequests();
    });
  }

  openAddToClass(subject: Subject, requestId: number){
    const dialogRef = this.dialog.open(AdmitRequestAddComponent,{
      width:'50%',
      enterAnimationDuration:'200ms',
      exitAnimationDuration:'200ms',
      data:{
        subject: subject,
        requestId: requestId
      }
    });

    dialogRef.afterClosed().subscribe(() => {
      this.loadNotAdmittedRequests();
    });
  }

  openProfile(userId: number){
    this.router.navigate([`/profile/${userId}`])
  }
}
