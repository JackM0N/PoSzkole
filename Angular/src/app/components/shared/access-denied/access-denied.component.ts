import { Component } from '@angular/core';
import { Location } from '@angular/common';

@Component({
  selector: 'app-access-denied',
  templateUrl: './access-denied.component.html',
  styleUrl: '../../../styles/access-denied.component.css'
})
export class AccessDeniedComponent {
  constructor(private location: Location) {}

  goBack(): void {
    console.log(this.location.back()) //This does not work without this line of code. IT HAS TO BE HERE
    this.location.back();
  }
}