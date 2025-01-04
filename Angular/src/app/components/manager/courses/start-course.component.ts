import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SubjectService } from '../../../services/subject.service';
import { Subject } from '../../../models/subject.model';
import { CompactUser } from '../../../models/compact-user.model';
import { map, Observable, startWith } from 'rxjs';
import { WebsiteUserService } from '../../../services/website-user.service';
import { DaysOfTheWeek } from '../../../enums/days-of-the-week.enum';
import { StartCourse } from '../../../models/start-course.model';
import { CourseService } from '../../../services/course.service';
import { ToastrService } from 'ngx-toastr';
import { Course } from '../../../models/course.model';

@Component({
  selector: 'app-start-course',
  templateUrl: './start-course.component.html',
  styleUrls: ['../../../styles/request-form.component.css'],
})
export class StartCourseComponent implements OnInit {
  startCourseForm: FormGroup;
  teachers: CompactUser[] = [];
  subjects: Subject[] = [];
  filteredTeachers!: Observable<CompactUser[]>;
  filteredSubjects!: Observable<Subject[]>;
  days: DaysOfTheWeek[] = [];
  
  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<StartCourseComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { course: Course },
    private subjectService: SubjectService,
    private websiteUserService: WebsiteUserService,
    private courseService: CourseService,
    private toastr: ToastrService
  ) {
    this.startCourseForm = this.fb.group({
      teacher: ['', Validators.required],
      className: ['', Validators.required],
      subject: ['', Validators.required],
      dayOfWeek: ['', Validators.required],
      timeFrom: ['', Validators.required],
      timeTo: ['', Validators.required],
      isOnline: [false],
      repeatUntil: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadStudents();
    this.loadSubjects();
    this.days = Object.values(DaysOfTheWeek);

    this.filteredTeachers = this.startCourseForm.get('teacher')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterTeachers(value || ''))
    );

    this.filteredSubjects = this.startCourseForm.get('subject')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterSubjects(value || ''))
    );
  }

  loadStudents() {
    this.websiteUserService.loadTeachers().subscribe(data => {
      this.teachers = data;
    });
  }

  loadSubjects() {
    this.subjectService.loadSubjects().subscribe(data => {
      this.subjects = data;
    });
  }

  private filterTeachers(value: string): CompactUser[] {
    if (typeof value !== 'string') return [];
    const filterValue = value.toLowerCase();
    return this.teachers?.filter(teacher =>
      `${teacher.firstName} ${teacher.lastName}`.toLowerCase().includes(filterValue)
    ) || [];
  }
  
  private filterSubjects(value: string): Subject[] {
    if (typeof value !== 'string') return [];
    const filterValue = value.toLowerCase();
    return this.subjects?.filter(subject =>
      subject?.subjectName?.toLowerCase().includes(filterValue)
    ) || [];
  }

  displayTeacher(teacher: CompactUser): string {
    return teacher ? `${teacher.id} - ${teacher.firstName} ${teacher.lastName}` : '';
  }
  
  displaySubject(subject: Subject): string {
    return subject ? subject.subjectName : '';
  }

  onSubmit(): void {
    if (this.startCourseForm.valid) {
      const formData = this.startCourseForm.value;
      
      //Converting polish values back to their original values
      const dayKey = Object.keys(DaysOfTheWeek).find(
        key => DaysOfTheWeek[key as keyof typeof DaysOfTheWeek] === formData.dayOfWeek
      );

      const startCourse: StartCourse = {
        courseId: this.data.course.id!,
        tutoringClassDTO: {
          className: formData.className,
          subject: formData.subject
        },
        teacherId: formData.teacher.id,
        dayAndTimeDTO: {
          day: dayKey || formData.dayOfWeek,
          timeFrom: formData.timeFrom + ':00',
          timeTo: formData.timeTo + ':00',
        },
        isOnline: formData.isOnline,
        repeatUntil: formData.repeatUntil,
      };

      this.courseService.startCourse(startCourse).subscribe({
        next: () => {
          this.toastr.success("Request admitted successfully!");
          this.dialogRef.close();
        },
        error: (error) => {
          if (error.error === "Class schedule overlaps with existing class") {
            this.toastr.error("Nie można dodać zajęć. Wybrane godziny nakładają się z innymi zajęciami.", "Błąd przyjęcia zajęć");
          } else {
            this.toastr.error("Wystąpił nieoczekiwany błąd", "Błąd");
            console.log("Start course error", error)
          }
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
