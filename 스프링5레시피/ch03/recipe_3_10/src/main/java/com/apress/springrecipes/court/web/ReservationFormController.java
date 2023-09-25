package com.apress.springrecipes.court.web;

import com.apress.springrecipes.court.domain.Player;
import com.apress.springrecipes.court.domain.Reservation;
import com.apress.springrecipes.court.domain.ReservationValidator;
import com.apress.springrecipes.court.domain.SportType;
import com.apress.springrecipes.court.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;


@Controller
@RequestMapping("/reservationForm")
@SessionAttributes("reservation")
public class ReservationFormController {

    private final ReservationService reservationService;

    private final ReservationValidator reservationValidator;

    public ReservationFormController(ReservationService reservationService, ReservationValidator reservationValidator) {
        this.reservationService = reservationService;
        this.reservationValidator = reservationValidator;
    }

    @ModelAttribute("sportTypes")
    public List<SportType> populateSportTypes() {
        return reservationService.getAllSportTypes();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(
            @RequestParam(required = false, value = "username") String username,
            Model model) {

        Reservation reservation = new Reservation();
        reservation.setPlayer(new Player(username, null));

        model.addAttribute("reservation", reservation);

        return "reservationForm";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitForm(
            @ModelAttribute("reservation") @Validated Reservation reservation,
            BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return "reservationForm";
        } else {
            reservationService.make(reservation);
            status.setComplete();
            return "redirect:reservationSuccess";
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(reservationValidator);
    }
}