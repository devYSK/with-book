package com.apress.springrecipes.court.web;

import com.apress.springrecipes.court.domain.Reservation;
import com.apress.springrecipes.court.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;


@Controller
@RequestMapping("/reservationForm")
@SessionAttributes("reservation")
public class ReservationFormController {

    private final ReservationService reservationService;

    public ReservationFormController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(
            @RequestParam(required = false, value = "username") String username,
            Model model) {

        Reservation reservation = new Reservation();
        model.addAttribute("reservation", reservation);

        return "reservationForm";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitForm(
            @ModelAttribute("reservation") Reservation reservation,
            BindingResult result, SessionStatus status) {
        reservationService.make(reservation);
        return "redirect:reservationSuccess";
    }
}