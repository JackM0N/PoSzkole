import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

interface Student {
  id: number;
  firstName: string;
  lastName: string;
}

interface Subject {
  id: number;
  subjectName: string;
}

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

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.requestForm = this.fb.group({
      student: new FormControl(''),
      subject: new FormControl(''),
      repeat: new FormControl(false)
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
    this.http.get<Student[]>('http://localhost:8080/user/all-students').subscribe(data => {
      this.students = data;
    });
  }

  loadSubjects() {
    this.http.get<Subject[]>('http://localhost:8080/subject/all').subscribe(data => {
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
      console.log('Form Value:', formValue);
      // handle sending
    }
  }

  resetForm() {
    this.requestForm.reset();
  }
}
