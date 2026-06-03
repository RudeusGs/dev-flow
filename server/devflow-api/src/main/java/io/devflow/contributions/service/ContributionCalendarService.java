package io.devflow.contributions.service;

import io.devflow.contributions.dto.ContributionBreakdownResponse;
import io.devflow.contributions.dto.ContributionCalendarDayResponse;
import io.devflow.contributions.dto.ContributionCalendarResponse;
import io.devflow.contributions.dto.ContributionEventResponse;
import io.devflow.contributions.dto.RecordContributionRequest;
import io.devflow.contributions.entity.ContributionDay;
import io.devflow.contributions.entity.ContributionEvent;
import io.devflow.contributions.enums.ContributionVisibility;
import io.devflow.contributions.repository.ContributionDayRepository;
import io.devflow.contributions.repository.ContributionEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContributionCalendarService {

    private static final int MAX_CALENDAR_DAYS = 370;

    private final ContributionDayRepository contributionDayRepository;
    private final ContributionEventRepository contributionEventRepository;
    private final Clock clock;

    public ContributionCalendarService(
            ContributionDayRepository contributionDayRepository,
            ContributionEventRepository contributionEventRepository
    ) {
        this.contributionDayRepository = contributionDayRepository;
        this.contributionEventRepository = contributionEventRepository;
        this.clock = Clock.systemUTC();
    }

    @Transactional(readOnly = true)
    public ContributionCalendarResponse getCalendar(
            UUID userId,
            LocalDate from,
            LocalDate to,
            boolean includePrivateContributions
    ) {
        LocalDate resolvedTo = to == null ? LocalDate.now(clock) : to;
        LocalDate resolvedFrom = from == null ? resolvedTo.minusYears(1).plusDays(1) : from;
        validateDateRange(resolvedFrom, resolvedTo);

        Map<LocalDate, ContributionDay> daysByDate = contributionDayRepository
                .findByUserIdAndContributionDateBetweenOrderByContributionDateAsc(userId, resolvedFrom, resolvedTo)
                .stream()
                .collect(Collectors.toMap(ContributionDay::getContributionDate, Function.identity()));

        ArrayList<ContributionCalendarDayResponse> days = new ArrayList<>();
        int total = 0;

        for (LocalDate date = resolvedFrom; !date.isAfter(resolvedTo); date = date.plusDays(1)) {
            ContributionDay contributionDay = daysByDate.get(date);
            ContributionCalendarDayResponse dayResponse = toCalendarDayResponse(
                    date,
                    contributionDay,
                    includePrivateContributions
            );
            total += dayResponse.contributionCount();
            days.add(dayResponse);
        }

        return new ContributionCalendarResponse(
                userId,
                resolvedFrom,
                resolvedTo,
                total,
                includePrivateContributions,
                days
        );
    }

    @Transactional
    public ContributionEventResponse recordContribution(RecordContributionRequest request) {
        return contributionEventRepository
                .findByUserIdAndContributionTypeAndSourceTypeAndSourceId(
                        request.userId(),
                        request.contributionType(),
                        request.sourceType(),
                        request.sourceId()
                )
                .map(this::toEventResponse)
                .orElseGet(() -> createContribution(request));
    }

    @Transactional(readOnly = true)
    public List<ContributionEventResponse> getEvents(
            UUID userId,
            LocalDate from,
            LocalDate to,
            boolean includePrivateContributions
    ) {
        LocalDate resolvedTo = to == null ? LocalDate.now(clock) : to;
        LocalDate resolvedFrom = from == null ? resolvedTo : from;
        validateDateRange(resolvedFrom, resolvedTo);

        return contributionEventRepository
                .findByUserIdAndContributionDateBetweenAndIgnoredFalseOrderByOccurredAtDesc(
                        userId,
                        resolvedFrom,
                        resolvedTo
                )
                .stream()
                .filter(event -> includePrivateContributions || event.isPublicContribution())
                .map(this::toEventResponse)
                .toList();
    }

    private ContributionEventResponse createContribution(RecordContributionRequest request) {
        ContributionVisibility visibility = request.visibility() == null
                ? ContributionVisibility.PUBLIC
                : request.visibility();
        Instant occurredAt = request.occurredAt() == null ? Instant.now(clock) : request.occurredAt();
        LocalDate contributionDate = LocalDate.ofInstant(occurredAt, ZoneOffset.UTC);
        int weight = request.weight() == null ? 1 : Math.max(request.weight(), 1);

        ContributionEvent event = new ContributionEvent();
        event.setUserId(request.userId());
        event.setRepositoryId(request.repositoryId());
        event.setContributionType(request.contributionType());
        event.setVisibility(visibility);
        event.setContributionDate(contributionDate);
        event.setOccurredAt(occurredAt);
        event.setSourceType(request.sourceType());
        event.setSourceId(request.sourceId());
        event.setWeight(weight);
        event.setMetadata(request.metadata() == null ? new HashMap<>() : new HashMap<>(request.metadata()));

        ContributionEvent savedEvent = contributionEventRepository.save(event);
        updateDailyAggregate(savedEvent);

        return toEventResponse(savedEvent);
    }

    private void updateDailyAggregate(ContributionEvent event) {
        ContributionDay day = contributionDayRepository
                .findByUserIdAndContributionDate(event.getUserId(), event.getContributionDate())
                .orElseGet(() -> newContributionDay(event.getUserId(), event.getContributionDate()));

        day.addContribution(event.getContributionType(), event.getVisibility(), event.getWeight());
        contributionDayRepository.save(day);
    }

    private ContributionDay newContributionDay(UUID userId, LocalDate contributionDate) {
        ContributionDay day = new ContributionDay();
        day.setUserId(userId);
        day.setContributionDate(contributionDate);
        return day;
    }

    private ContributionCalendarDayResponse toCalendarDayResponse(
            LocalDate date,
            ContributionDay day,
            boolean includePrivateContributions
    ) {
        if (day == null) {
            return new ContributionCalendarDayResponse(date, 0, 0, 0, includePrivateContributions ? 0 : null, null);
        }

        int contributionCount = includePrivateContributions ? day.getTotalCount() : day.getPublicCount();
        ContributionBreakdownResponse breakdown = includePrivateContributions
                ? new ContributionBreakdownResponse(
                        day.getCommitCount(),
                        day.getIssueCount(),
                        day.getPullRequestCount(),
                        day.getReviewCount(),
                        day.getCommentCount(),
                        day.getReleaseCount(),
                        day.getRepositoryCount(),
                        day.getOtherCount()
                )
                : null;

        return new ContributionCalendarDayResponse(
                date,
                contributionCount,
                contributionLevel(contributionCount),
                day.getPublicCount(),
                includePrivateContributions ? day.getPrivateCount() : null,
                breakdown
        );
    }

    private ContributionEventResponse toEventResponse(ContributionEvent event) {
        return new ContributionEventResponse(
                event.getId(),
                event.getUserId(),
                event.getRepositoryId(),
                event.getContributionType(),
                event.getVisibility(),
                event.getContributionDate(),
                event.getOccurredAt(),
                event.getSourceType(),
                event.getSourceId(),
                event.getWeight(),
                event.isIgnored(),
                event.getMetadata()
        );
    }

    private int contributionLevel(int count) {
        if (count <= 0) {
            return 0;
        }
        if (count <= 2) {
            return 1;
        }
        if (count <= 5) {
            return 2;
        }
        if (count <= 9) {
            return 3;
        }
        return 4;
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must be before or equal to to");
        }

        long days = ChronoUnit.DAYS.between(from, to) + 1;
        if (days > MAX_CALENDAR_DAYS) {
            throw new IllegalArgumentException("contribution calendar range cannot exceed " + MAX_CALENDAR_DAYS + " days");
        }
    }
}
