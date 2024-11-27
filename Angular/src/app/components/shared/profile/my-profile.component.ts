import { Component, OnInit } from '@angular/core';
import { WebsiteUser } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrl: '../../../styles/account.component.css'
})
export class MyAccountComponent implements OnInit{
  currentUser?: WebsiteUser;

  constructor(private websiteUserService: WebsiteUserService){}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.websiteUserService.loadCurrentUserProfile().subscribe({
      next: response => {
        this.currentUser = response;
      },
      error: error => {
        console.error("Wystąpił problem z wczytaniem twojego profilu", error);
      }
    })
  }
}
