import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { RequestService } from "../../../services/request.service";
import { Observer } from "rxjs";

@Component({
  selector: 'app-request-admitted-list',
  templateUrl: './request-admitted-list.component.html',
  styleUrl: '../../../styles/request-list.component.css'
})
export class RequestAdmittedListComponent implements AfterViewInit{
  protected dataSource: MatTableDataSource<Request> = new MatTableDataSource<Request>([]);
  protected totalRequests: number = 0;
  protected UnadmittedDisplayedColumns: string[] = ['student', 'subject.subjectName', 'repeatUntil', 'prefersIndividual', 'prefersLocation', 'issueDate', 'acceptanceDate'];
  protected noRequests = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(private requestService: RequestService){}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadAdmittedRequests();

    this.sort.sortChange.subscribe(() => {
      this.loadAdmittedRequests();
    });
  }

  loadAdmittedRequests(){
    const page = this.paginator.pageIndex + 1;
    const size = this.paginator.pageSize;
    const sortBy = this.sort.active || 'id';
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
        console.error(error);
      },
      complete: () => {}
    };
    this.requestService.getAdmittedRequests(page, size, sortBy, sortDir).subscribe(observer);
  }
}
