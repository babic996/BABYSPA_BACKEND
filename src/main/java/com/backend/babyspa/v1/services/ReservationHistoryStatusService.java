package com.backend.babyspa.v1.services;

import com.backend.babyspa.v1.models.Reservation;
import com.backend.babyspa.v1.models.ReservationHistoryStatus;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.repositories.ReservationHistoryStatusRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationHistoryStatusService {

    @Autowired
    ReservationHistoryStatusRepository reservationHistoryStatusRepository;

    @Transactional
    public void save(Reservation reservation, Status status, User actionByUser) {
        ReservationHistoryStatus reservationHistoryStatus = new ReservationHistoryStatus();

        reservationHistoryStatus.setReservation(reservation);
        reservationHistoryStatus.setStatus(status);
        reservationHistoryStatus.setActionByUser(actionByUser);

        reservationHistoryStatusRepository.save(reservationHistoryStatus);
    }
}
