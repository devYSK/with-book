package com.apress.springrecipes.court.web;

import com.apress.springrecipes.court.Delayer;
import com.apress.springrecipes.court.domain.Reservation;
import com.apress.springrecipes.court.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.Callable;


@Controller
@RequestMapping("/reservationQuery")
public class ReservationQueryController {

    private final ReservationService reservationService;

    public ReservationQueryController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public void setupForm() {}

    @PostMapping
    public Callable<String> sumbitForm(@RequestParam("courtName") String courtName, Model model) {
        return () -> {
            List<Reservation> reservations = java.util.Collections.emptyList();

            if (courtName != null) {
                Delayer.randomDelay();
                reservations = reservationService.query(courtName);
            }

            model.addAttribute("reservations", reservations);

            return "reservationQuery";
        };
    }
}