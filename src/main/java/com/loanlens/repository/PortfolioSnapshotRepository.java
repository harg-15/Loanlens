package com.loanlens.repository;

import com.loanlens.entity.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PortfolioSnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
    Optional<PortfolioSnapshot> findTopByOrderBySnapshotDateDesc();
    List<PortfolioSnapshot> findTop30ByOrderBySnapshotDateDesc();
    Optional<PortfolioSnapshot> findBySnapshotDate(LocalDate date);
    boolean existsBySnapshotDate(LocalDate date);
}
