package in.shubhamprakash681.notification_service.controllers;

import in.shubhamprakash681.common_lib.security.JwtPrincipal;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.AlertRequest;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.AlertResponse;
import in.shubhamprakash681.notification_service.services.AlertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AlertResponse create(@AuthenticationPrincipal JwtPrincipal principal,
                         @Valid @RequestBody AlertRequest request) {
        return alertService.create(principal, request);
    }

    @GetMapping
    List<AlertResponse> alerts(@AuthenticationPrincipal JwtPrincipal principal) {
        return alertService.alerts(principal);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal JwtPrincipal principal,
                @RequestParam(required = false) Long id,
                @RequestParam(required = false) String symbol) {
        alertService.delete(principal, id, symbol);
    }
}
