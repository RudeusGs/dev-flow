package io.devflow.contributions.repository;

import io.devflow.contributions.entity.ContributionEvent;
import io.devflow.contributions.enums.ContributionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContributionEventRepository extends JpaRepository<ContributionEvent, UUID> {

    Optional<ContributionEvent> findByUserIdAndContributionTypeAndSourceTypeAndSourceId(
            UUID userId,
            ContributionType contributionType,
            String sourceType,
            UUID sourceId
    );

    List<ContributionEvent> findByUserIdAndContributionDateBetweenAndIgnoredFalseOrderByOccurredAtDesc(
            UUID userId,
            LocalDate from,
            LocalDate to
    );
}
