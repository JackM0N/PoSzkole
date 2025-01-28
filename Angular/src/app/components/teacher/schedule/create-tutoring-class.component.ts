import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { TutoringClassService } from '../../../services/tutoring-class.service';
import { DaysOfTheWeek } from '../../../enums/days-of-the-week.enum';
import { StudentRequestAndDate } from '../../../models/student-request-and-date.model';
import { timeOrderValidator } from '../../../validators/time-order.validator';
import { CompactUser } from '../../../models/compact-user.model';
import { Subject } from '../../../models/subject.model';
import { map, Observable, startWith } from 'rxjs';
import { WebsiteUserService } from '../../../services/website-user.service';
import { SubjectService } from '../../../services/subject.service';

@Component({
  selector: 'app-create-tutoring-class',
  templateUrl: './create-tutoring-class.component.html',
  styleUrl: '../../../styles/request-form.component.css'
})
export class CreateTutoringClassComponent implements OnInit {
  createClassForm: FormGroup;
  days: DaysOfTheWeek[] = [];
  students: CompactUser[] = [];
  subjects: Subject[] = [];
  filteredStudents!: Observable<CompactUser[]>;
  filteredSubjects!: Observable<Subject[]>;

  constructor(
    private dialogRef: MatDialogRef<CreateTutoringClassComponent>,
    private fb: FormBuilder,
    private tutoringClassService: TutoringClassService,
    private websiteUserService: WebsiteUserService,
    private subjectService: SubjectService,
    private toastr: ToastrService
  ) {
    this.createClassForm = this.fb.group({
      student: new FormControl(null, [Validators.required]),
      tutoringClassDTO: this.fb.group({
        className: new FormControl(null, [Validators.required]),
        subject: new FormControl(null, [Validators.required])
      }),
      repeat: new FormControl(false),
      repeatUntil: new FormControl(null),
      dayAndTimeDTO: this.fb.group({
        day: new FormControl(null, [Validators.required]),
        timeFrom: new FormControl(null, [Validators.required]),
        timeTo: new FormControl(null, [Validators.required]),
      }, { validators: timeOrderValidator }),
      isOnline: new FormControl(false),
    });
  }

  ngOnInit(): void {
    this.days = Object.values(DaysOfTheWeek);
    this.loadStudents();
    this.loadSubjects();

    this.filteredStudents = this.createClassForm.get('student')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterStudents(value || ''))
    );

    this.filteredSubjects = this.createClassForm.get('tutoringClassDTO')!.get('subject')!.valueChanges.pipe(
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
    this.subjectService.loadCurrentTeacherSubjects().subscribe(data => {
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

  onSubmit(): void {
    if (this.createClassForm.valid) {
      const formData = this.createClassForm.value;
      
      if (!formData.repeat) {
        formData.repeatUntil = null;
      }

      //Converting polish values back to their original values
      const dayKey = Object.keys(DaysOfTheWeek).find(
        key => DaysOfTheWeek[key as keyof typeof DaysOfTheWeek] === formData.dayAndTimeDTO.day
      );

      const createTutorinClass: StudentRequestAndDate = {
        studentId: formData.student.id,
        tutoringClassDTO: formData.tutoringClassDTO,
        dayAndTimeDTO: {
          day: dayKey || formData.dayAndTimeDTO.day,
          timeFrom: formData.dayAndTimeDTO.timeFrom + ':00',
          timeTo: formData.dayAndTimeDTO.timeTo + ':00'
        },
        repeatUntil: formData.repeatUntil,
        isOnline: formData.isOnline
      }

      console.log(createTutorinClass)

      this.tutoringClassService.createTutoringClass(createTutorinClass).subscribe({
        next: () => {
          this.toastr.success('Zajęcia zostały utworzone!', 'Sukces!');
          this.dialogRef.close(true);
        },
        error: (error) => {
          if (error.error === "Class schedule overlaps with existing student's class") {
            this.toastr.error("Nie można dodać zajęć.Wybrany termin nakłada się z innymi zajęciami w harmonogramie ucznia.", "Błąd");
          } else if (error.error === "Class schedule overlaps with existing teacher's class") {
            this.toastr.error("Nie można dodać zajęć. Wybrany termin nakłada się z innymi zajęciami w twoim harmonogramie.", "Błąd");
          } else if (error.error === "You cannot create class on users busy day") {
            this.toastr.error("Nie można utworzyć zajęć. Wybrany termin zajmuje się w czasie, gdy uczeń jest niedostępny.", "Błąd");
          } else {
            this.toastr.error("Wystąpił nieoczekiwany błąd", "Błąd");
            console.error("Request update error", error)
          }
        }
      });
    }
  }

  closeDialog(): void {
    this.dialogRef.close();
  }
}
