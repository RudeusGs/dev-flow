package io.devflow.contributions.repository;

import io.devflow.contributions.entity.ContributionDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContributionDayRepository extends JpaRepository<ContributionDay, UUID> {

    Optional<ContributionDay> findByUserIdAndContributionDate(UUID userId, LocalDate contributionDate);

    List<ContributionDay> findByUserIdAndContributionDateBetweenOrderByContributionDateAsc(
            UUID userId,
            LocalDate from,
            LocalDate to
    );
}
