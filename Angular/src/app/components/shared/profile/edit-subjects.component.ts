import { Component, Inject, OnInit } from '@angular/core';
import { Subject } from '../../../models/subject.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { SubjectService } from '../../../services/subject.service';
import { ToastrService } from 'ngx-toastr';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';


@Component({
  selector: 'app-edit-subjects',
  templateUrl: './edit-subjects.component.html',
  styleUrls: ['../../../styles/edit-subjects.component.css']
})
export class EditSubjectsComponent implements OnInit{
  allSubjects: Subject[] = [];
  newSubjects: Subject[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data:{
      teacherId: number;
      subjects: Subject[];
    },
    private dialogRef: MatDialogRef<EditSubjectsComponent>,
    private subjectService: SubjectService,
    private websiteUserService: WebsiteUserService,
    private toastr: ToastrService
  ){}

  ngOnInit(): void {
    this.loadSubjects();
  }

  loadSubjects() {
    this.subjectService.loadSubjects().subscribe({
      next: response => {
        this.allSubjects = response;
        this.newSubjects = [...this.data.subjects];
      },
      error: error => {
        this.toastr.error("Coś poszło nie tak podczas próby wczytania przedmiotów");
        console.error('Błąd wczytywania przedmiotów', error);
      }
    })
  }

  isSubjectSelected(subject: Subject): boolean {
    return this.newSubjects.some(selectedSubject => selectedSubject.id === subject.id);
  }

  toggleSubject(subject: Subject, isChecked: boolean): void {
    if (isChecked) {
      this.newSubjects.push(subject);
    } else {
      this.newSubjects = this.newSubjects.filter(selectedSubject => selectedSubject.id !== subject.id);
    }
  }

  onSubmit() {
    this.websiteUserService.editTeacherSubjects(this.data.teacherId, this.newSubjects).subscribe({
      next: response => {
        this.toastr.success("Twoje przedmioty zostały zaktualizowane");
        this.dialogRef.close(response);
      },
      error: error => {
        this.toastr.error("Coś poszło nie tak podczas edytowania przedmiotów");
        console.error('Błąd edycji przedmiotów', error);
      }
    })
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
