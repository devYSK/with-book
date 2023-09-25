package com.apress.springrecipes.court.web;

import com.apress.springrecipes.court.domain.Player;
import com.apress.springrecipes.court.domain.Reservation;
import com.apress.springrecipes.court.domain.SportType;
import com.apress.springrecipes.court.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;


@Controller
@RequestMapping("/reservationForm")
@SessionAttributes("reservation")
public class ReservationFormController {

    private final ReservationService reservationService;

    public ReservationFormController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @ModelAttribute("sportTypes")
    public List<SportType> populateSportTypes() {
        return reservationService.getAllSportTypes();
    }

    @GetMapping
    public String setupForm(
            @RequestParam(required = false, value = "username") String username,
            Model model) {
        Reservation reservation = new Reservation();
        reservation.setPlayer(new Player(username, null));

        model.addAttribute("reservation", reservation);

        return "reservationForm";
    }

    @PostMapping
    public Callable<String> submitForm(
            @ModelAttribute("reservation") @Valid Reservation reservation,
            BindingResult result, SessionStatus status) {
        return () -> {
            if (result.hasErrors()) {
                return "reservationForm";
            } else {
                Thread.sleep(new Random().nextInt(10) * 120);
                reservationService.make(reservation);
                status.setComplete();
                return "redirect:reservationSuccess";
            }
        };
    }
}
