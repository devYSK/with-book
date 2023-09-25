package com.apress.springrecipes.court.web;

import com.apress.springrecipes.court.Delayer;
import com.apress.springrecipes.court.domain.Reservation;
import com.apress.springrecipes.court.service.ReservationService;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;


@Controller
@RequestMapping("/reservationQuery")
public class ReservationQueryController {

    private final ReservationService reservationService;
    private final TaskExecutor taskExecutor;

    public ReservationQueryController(ReservationService reservationService, TaskExecutor taskExecutor) {
        this.reservationService = reservationService;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping
    public void setupForm() {
    }

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

    @GetMapping(params = "courtName")
    public SseEmitter find(@RequestParam("courtName") String courtName) {
        final SseEmitter emitter = new SseEmitter();
        taskExecutor.execute(() -> {
            Collection<Reservation> reservations = reservationService.query(courtName);
            try {
                for (Reservation reservation : reservations) {
                    Delayer.delay(120);
                    emitter.send(SseEmitter.event().id(String.valueOf(reservation.hashCode())).data(reservation));
                }
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}