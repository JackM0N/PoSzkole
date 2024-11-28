import { Component, OnInit } from '@angular/core';
import { WebsiteUser } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: '../../../styles/profile.component.css'
})
export class UserAccountComponent implements OnInit{
  user?: WebsiteUser;

  constructor(
    private websiteUserService: WebsiteUserService,
    private route: ActivatedRoute,
  ){}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile() {
    const userId = this.route.snapshot.paramMap.get('id');
    this.websiteUserService.loadUserProfile(+userId!).subscribe({
      next: response => {
        this.user = response;
      },
      error: error => {
        console.error("Wystąpił problem z wczytaniem profilu użytkownika", error);
      }
    })
  }
}
