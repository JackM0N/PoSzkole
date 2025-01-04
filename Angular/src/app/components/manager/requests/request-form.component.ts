import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { CompactUser } from '../../../models/compact-user.model';
import { Subject } from '../../../models/subject.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { SubjectService } from '../../../services/subject.service';
import { RequestService } from '../../../services/request.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { RegisterStudentComponent } from '../students/register-student.component';

@Component({
  selector: 'app-request-form',
  templateUrl: './request-form.component.html',
  styleUrls: ['../../../styles/request-form.component.css']
})
export class RequestFormComponent implements OnInit {
  requestForm: FormGroup;
  students: CompactUser[] = [];
  subjects: Subject[] = [];
  filteredStudents!: Observable<CompactUser[]>;
  filteredSubjects!: Observable<Subject[]>;

  constructor(
    private fb: FormBuilder, 
    private websiteUserService: WebsiteUserService,
    private subjectService: SubjectService,
    private requestService: RequestService,
    private toastr: ToastrService,
    private dialog: MatDialog) {
    this.requestForm = this.fb.group({
      student: new FormControl(''),
      subject: new FormControl(''),
      repeat: new FormControl(false),
      repeatUntil: new FormControl(null),
      prefersIndividual: new FormControl(false),
      prefersLocation: new FormControl('')
    });
  }

  ngOnInit(): void {
    this.loadStudents();
    this.loadSubjects();

    this.filteredStudents = this.requestForm.get('student')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterStudents(value || ''))
    );

    this.filteredSubjects = this.requestForm.get('subject')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterSubjects(value || ''))
    );
  }

  loadStudents() {
    this.websiteUserService.loadStudents().subscribe(data => {
      this.students = data;
    });
  }

  loadSubjects() {
    this.subjectService.loadSubjects().subscribe(data => {
      this.subjects = data;
    });
  }

  private filterStudents(value: string): CompactUser[] {
    if (typeof value !== 'string') return [];
    const filterValue = value.toLowerCase();
    return this.students?.filter(student =>
      `${student.firstName} ${student.lastName}`.toLowerCase().includes(filterValue)
    ) || [];
  }
  
  private filterSubjects(value: string): Subject[] {
    if (typeof value !== 'string') return [];
    const filterValue = value.toLowerCase();
    return this.subjects?.filter(subject =>
      subject?.subjectName?.toLowerCase().includes(filterValue)
    ) || [];
  }

  displayStudent(student: CompactUser): string {
    return student ? `${student.id} - ${student.firstName} ${student.lastName}` : '';
  }
  
  displaySubject(subject: Subject): string {
    return subject ? subject.subjectName : '';
  }

  onSubmit() {
    if (this.requestForm.valid) {
      const formValue = this.requestForm.value;
      
      if (!formValue.repeat) {
        formValue.repeatUntil = null;
      }

      this.requestService.createRequest(formValue).subscribe({
        next: () => {
          this.toastr.success("Pomyślnie utworzono prośbę o stworzenie nowych zajęć");
          this.resetForm();
        },
        error: error => {
          this.toastr.error("Coś poszło nie tak podczas próby stworzenia prośby");
          console.error('Request creation error', error);
        }
      });
    }
  }

  resetForm() {
    this.requestForm.reset();
  }

  openRegisterStudent() {
    const dialogRef = this.dialog.open(RegisterStudentComponent, {
      width: '30%'
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log("Add displaying newly added student")
      }
    });
  }
}
