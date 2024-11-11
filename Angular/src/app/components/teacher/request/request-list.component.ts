import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Request } from '../../../models/request.model';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { RequestService } from '../../../services/request.service';
import { Observer } from 'rxjs';

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
  ){}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.loadUnadmittedRequests();

    this.sort.sortChange.subscribe(() => {
      this.loadUnadmittedRequests();
    });
  }

  ngOnInit() {
    this.dataSource.sortingDataAccessor = (item, property) => {
      const propertyAccessors: { [key: string]: any } = {
        'Uczeń': item.student!.firstName + ' ' + item.student!.lastName,
        'Przedmiot': item.subject!.subjectName,
        'Data zakończenia zajęć': item.repeatUntil,
        'Preferuje indywidualnie': item.prefersIndividual,
        'Preferowany tryb zajęć': item.prefersLocation,
        'Data utworzenia': item.issueDate
      };
      return propertyAccessors[property] || item[property as keyof Request];
    };
  }

  loadUnadmittedRequests(){
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
    this.requestService.getNotAdmittedRequests(page, size, sortBy, sortDir).subscribe(observer);
  }
}
