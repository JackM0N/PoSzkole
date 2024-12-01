import { Component, Inject, Input, OnInit } from '@angular/core';
import { Room } from '../../../models/room.model';
import { RoomReservationService } from '../../../services/room-reservation.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { DateTime } from 'luxon';

@Component({
  selector: 'app-reserve-room',
  templateUrl: './reserve-room.component.html',
  styleUrl: '../../../styles/reserve-room.component.css'
})
export class ReserveRoomComponent implements OnInit{
  rooms: Room[] = [];
  @Input() importData!: { 
    id: number | null; 
    classDateFrom: DateTime; 
    classDateTo: DateTime; 
  };

  constructor(
    public dialogRef: MatDialogRef<ReserveRoomComponent>,
    private roomReservationService: RoomReservationService,
    @Inject(MAT_DIALOG_DATA) public data:{
      id: number;
      classDateFrom: DateTime;
      classDateTo: DateTime;
    },
    private toastr: ToastrService,
  ){}

  ngOnInit(): void {
    this.loadAvailableRooms()
  }

  loadAvailableRooms() {
    if (this.data.classDateFrom == null){
      this.data.classDateFrom = this.importData.classDateFrom;
      this.data.classDateTo = this.importData.classDateTo;
    }

    this.roomReservationService.getRoomsForSchedule(
      this.data.classDateFrom.toISO({ includeOffset: false })!, 
      this.data.classDateTo.toISO({ includeOffset: false })!
    ).subscribe({
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
    if (this.data.id === null){
      this.toastr.error('Nie można zarezerwować sali');
      return;
    }

    this.roomReservationService.reserveRoom(roomId, this.data.id).subscribe({
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
