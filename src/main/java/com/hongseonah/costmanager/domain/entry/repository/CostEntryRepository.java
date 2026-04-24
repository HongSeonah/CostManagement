package com.hongseonah.costmanager.domain.entry.repository;

import com.hongseonah.costmanager.domain.entry.entity.CostEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CostEntryRepository extends JpaRepository<CostEntry, Long> {
    List<CostEntry> findByEntryDateBetween(LocalDate start, LocalDate end);
    List<CostEntry> findByProjectId(Long projectId);
}

