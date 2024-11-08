import { Component, ViewChild } from '@angular/core';
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
export class RequestListComponent {
  protected dataSource: MatTableDataSource<Request> = new MatTableDataSource<Request>([]);
  protected totalRequests: number = 0;
  protected UnadmittedDisplayedColumns: string[] = ['Uczeń', 'Przedmiot', 'Data zakończenia zajęć', 'Preferuje indywidualnie', 'Preferowany tryb zajęć', 'Akcje'];
  protected noRequests = false;

  @ViewChild('paginator') protected paginator!: MatPaginator;
  @ViewChild(MatSort) protected sort!: MatSort;

  constructor(
    private requestService: RequestService,
  ){}

  loadUnadmittedRequests(){
    const page = this.paginator.pageIndex + 1;
    const size = this.paginator.pageSize;
    const sortBy = this.sort.active || 'id';
    const sortDir = this.sort.direction || 'asc';

    const observer: Observer<any> = {
      next: response => {
        if (response) {
          this.totalRequests = response.totalRequests;
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
    this.requestService.getUnadmittedRequests(page, size, sortBy, sortDir).subscribe(observer);
  }
}
