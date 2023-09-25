package com.apress.springrecipes.court.web;

import com.apress.springrecipes.court.domain.Reservation;
import com.apress.springrecipes.court.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/reservationSummary*")
public class ReservationSummaryController {

    private final ReservationService reservationService;

    public ReservationSummaryController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String generateSummary(
            @RequestParam(required = true, value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate, Model model) {

        List<Reservation> reservations = reservationService.findByDate(selectedDate);

        model.addAttribute("reservations", reservations);

        return "reservationSummary";
    }

    @ExceptionHandler
    public void handle(ServletRequestBindingException ex, @RequestParam(required = true, value = "date") String date) {
        if (ex.getRootCause() instanceof ParseException) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.getRootCause().printStackTrace(pw);
            throw new ReservationWebException("Invalid date format for reservation summary", new Date(), sw.toString());
        }
    }
}
