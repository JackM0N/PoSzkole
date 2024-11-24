import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Student } from '../../../models/student.model';
import { Subject } from '../../../models/subject.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { SubjectService } from '../../../services/subject.service';
import { RequestService } from '../../../services/request.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-request-form',
  templateUrl: './request-form.component.html',
  styleUrls: ['../../../styles/request-form.component.css']
})
export class RequestFormComponent implements OnInit {
  requestForm: FormGroup;
  students: Student[] = [];
  subjects: Subject[] = [];
  filteredStudents!: Observable<Student[]>;
  filteredSubjects!: Observable<Subject[]>;

  constructor(
    private fb: FormBuilder, 
    private websiteUserService: WebsiteUserService,
    private subjectService: SubjectService,
    private requestService: RequestService,
    private toastr: ToastrService) {
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

  private filterStudents(value: string): Student[] {
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

  displayStudent(student: Student): string {
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
        next: response => {
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
}
