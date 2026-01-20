package com.backend.babyspa.v1.services;

import com.backend.babyspa.v1.models.Reservation;
import com.backend.babyspa.v1.models.ReservationHistoryStatus;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.repositories.ReservationHistoryStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationHistoryStatusService {

  @Autowired private ReservationHistoryStatusRepository reservationHistoryStatusRepository;

  @Transactional
  public void save(Reservation reservation, Status status, User actionByUser) {
    ReservationHistoryStatus reservationHistoryStatus = new ReservationHistoryStatus();

    reservationHistoryStatus.setReservation(reservation);
    reservationHistoryStatus.setStatus(status);
    reservationHistoryStatus.setActionByUser(actionByUser);

    reservationHistoryStatusRepository.save(reservationHistoryStatus);
  }
}
