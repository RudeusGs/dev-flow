package io.devflow.contributions.controller;

import io.devflow.contributions.dto.ContributionCalendarResponse;
import io.devflow.contributions.dto.ContributionErrorResponse;
import io.devflow.contributions.dto.ContributionEventResponse;
import io.devflow.contributions.dto.RecordContributionRequest;
import io.devflow.contributions.service.ContributionCalendarService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1")
public class ContributionController {

    private final ContributionCalendarService contributionCalendarService;

    public ContributionController(ContributionCalendarService contributionCalendarService) {
        this.contributionCalendarService = contributionCalendarService;
    }

    @GetMapping("/users/{userId}/contributions/calendar")
    public ContributionCalendarResponse getCalendar(
            @PathVariable UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "false") boolean includePrivate
    ) {
        return contributionCalendarService.getCalendar(userId, from, to, includePrivate);
    }

    @PostMapping("/contributions/events")
    public ResponseEntity<ContributionEventResponse> recordContribution(
            @Valid @RequestBody RecordContributionRequest request
    ) {
        ContributionEventResponse response = contributionCalendarService.recordContribution(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{userId}/contributions/events")
    public List<ContributionEventResponse> getEvents(
            @PathVariable UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "false") boolean includePrivate
    ) {
        return contributionCalendarService.getEvents(userId, from, to, includePrivate);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ContributionErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ContributionErrorResponse(exception.getMessage()));
    }
}
