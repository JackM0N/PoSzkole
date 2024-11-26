import { Component, Inject, OnInit } from '@angular/core';
import { Room } from '../../../models/room.model';
import { RoomReservationService } from '../../../services/room-reservation.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClassSchedule } from '../../../models/class-schedule.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-reserve-room',
  templateUrl: './reserve-room.component.html',
  styleUrl: '../../../styles/reserve-room.component.css'
})
export class ReserveRoomComponent implements OnInit{
  rooms: Room[] = [];

  constructor(
    public dialogRef: MatDialogRef<ReserveRoomComponent>,
    private roomReservationService: RoomReservationService,
    @Inject(MAT_DIALOG_DATA) public data: ClassSchedule,
    private toastr: ToastrService,
  ){}

  ngOnInit(): void {
    this.loadAvailableRooms()
  }

  loadAvailableRooms() {
    this.roomReservationService.getRoomsForSchedule(this.data.id!).subscribe({
      next: response => {
        this.rooms = response;
      },
      error: error => {
        console.error('Błąd podczas wczytywania sal', error);
        this.toastr.error('Nie udało się wczytać sal');
      }
    })
  }

  reserveRoom(roomId: number) {
    this.roomReservationService.reserveRoom(roomId, this.data.id!).subscribe({
      next: response => {
        this.toastr.success('Sala została pomyślnie zarezerwowana');
        this.dialogRef.close(true);
      },
      error: error => {
        console.error('Błąd podczas rezerwacji sali', error);
        this.toastr.error('Nie udało się zarezerwować sali');
      }
    });
  }

}
