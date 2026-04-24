package com.hongseonah.costmanager.domain.allocation.repository;

import com.hongseonah.costmanager.domain.allocation.entity.MonthlySettlement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlySettlementRepository extends JpaRepository<MonthlySettlement, Long> {

    Optional<MonthlySettlement> findByMonth(String month);

    boolean existsByMonth(String month);
}
