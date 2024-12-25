import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-pop-up',
  templateUrl: './pop-up-dialog.component.html',
  styleUrl: '../../../styles/pop-up-dialog.component.css'
})
export class PopUpDialogComponent implements OnInit{
  constructor(
    private ref: MatDialogRef<PopUpDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{
      title: string;
      content: string;
      submitText: string;
      cancelText: string;
    }){}

  ngOnInit(): void {
    this.data.submitText = this.data.submitText || 'Submit';
    this.data.cancelText = this.data.cancelText || 'Cancel';
  }

  closePopup(confirm: boolean){
    this.ref.close(confirm);
  }
}
