package com.apress.springrecipes.court.web;

import com.apress.springrecipes.court.Delayer;
import com.apress.springrecipes.court.domain.Reservation;
import com.apress.springrecipes.court.service.ReservationService;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;


@Controller
@RequestMapping("/reservationQuery")
public class ReservationQueryController {

    private final ReservationService reservationService;
    private final TaskExecutor taskExecutor;

    public ReservationQueryController(ReservationService reservationService, AsyncTaskExecutor taskExecutor) {
        this.reservationService = reservationService;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping
    public void setupForm() {}

    @PostMapping
    public DeferredResult<String> sumbitForm(@RequestParam("courtName") String courtName, Model model) {
        final DeferredResult<String> result = new DeferredResult<>();

        taskExecutor.execute(() -> {
            List<Reservation> reservations = java.util.Collections.emptyList();

            if (courtName != null) {
                Delayer.randomDelay();
                reservations = reservationService.query(courtName);
            }

            model.addAttribute("reservations", reservations);

            result.setResult("reservationQuery");
        });

        return result;

    }
}