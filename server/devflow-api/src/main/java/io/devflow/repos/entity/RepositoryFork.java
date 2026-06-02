package io.devflow.repos.entity;

import io.devflow.common.entity.CreatedEntity;
import io.devflow.users.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "repository_forks")
public class RepositoryFork extends CreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_repository_id", nullable = false)
    private Repository sourceRepository;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fork_repository_id", nullable = false)
    private Repository forkRepository;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forked_by_id", nullable = false)
    private User forkedBy;
}
