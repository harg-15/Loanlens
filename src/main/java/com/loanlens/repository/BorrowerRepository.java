package com.loanlens.repository;

import com.loanlens.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    Optional<Borrower> findByPanNumber(String panNumber);
    boolean existsByPanNumber(String panNumber);
    boolean existsByEmail(String email);
}