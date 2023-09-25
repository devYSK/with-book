package com.ys.a.service;

import java.time.LocalDate;
import java.util.List;

import com.ys.a.domain.PeriodicReservation;
import com.ys.a.domain.Reservation;
import com.ys.a.domain.SportType;

public interface ReservationService {

    List<Reservation> query(String courtName);

    void make(Reservation reservation)
            throws ReservationNotAvailableException;

    List<SportType> getAllSportTypes();

    SportType getSportType(int sportTypeId);

    List<Reservation> findByDate(LocalDate summaryDate);

    void makePeriodic(PeriodicReservation periodicReservation)
            throws ReservationNotAvailableException;
}
