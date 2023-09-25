package com.ys.a.domain;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.ys.a.service.ReservationService;

@Component
public class SportTypeConverter implements Converter<String, SportType> {

    private final ReservationService reservationService;

    public SportTypeConverter(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public SportType convert(String source) {
        int sportTypeId = Integer.parseInt(source);
        SportType sportType = reservationService.getSportType(sportTypeId);
        return sportType;
    }
}
