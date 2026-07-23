package in.shubhamprakash681.notification_service.controllers;

import in.shubhamprakash681.common_lib.security.JwtPrincipal;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.WatchlistRequest;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.WatchlistResponse;
import in.shubhamprakash681.notification_service.services.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    List<WatchlistResponse> watchlist(@AuthenticationPrincipal JwtPrincipal principal) {
        return watchlistService.watchlist(principal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    WatchlistResponse add(@AuthenticationPrincipal JwtPrincipal principal,
                          @Valid @RequestBody WatchlistRequest request) {
        return watchlistService.add(principal, request);
    }

    @DeleteMapping("/{symbol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void remove(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable String symbol) {
        watchlistService.remove(principal, symbol);
    }
}
