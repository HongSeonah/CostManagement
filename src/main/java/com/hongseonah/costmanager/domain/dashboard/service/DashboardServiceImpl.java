package com.hongseonah.costmanager.domain.dashboard.service;

import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.dashboard.dto.response.BusinessUnitDashboardResponse;
import com.hongseonah.costmanager.domain.dashboard.dto.response.DashboardSummaryResponse;
import com.hongseonah.costmanager.domain.entry.repository.CostEntryRepository;
import com.hongseonah.costmanager.domain.project.entity.ProjectStatus;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final BusinessUnitRepository businessUnitRepository;
    private final ProjectRepository projectRepository;
    private final CostEntryRepository costEntryRepository;

    public DashboardServiceImpl(BusinessUnitRepository businessUnitRepository,
                                ProjectRepository projectRepository,
                                CostEntryRepository costEntryRepository) {
        this.businessUnitRepository = businessUnitRepository;
        this.projectRepository = projectRepository;
        this.costEntryRepository = costEntryRepository;
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        var projects = projectRepository.findAll();
        var businessUnits = businessUnitRepository.findAll();
        var entries = costEntryRepository.findByEntryDateBetween(LocalDate.now().withDayOfMonth(1), LocalDate.now());

        BigDecimal totalBudget = projects.stream()
                .map(project -> project.getBudgetAmount() == null ? BigDecimal.ZERO : project.getBudgetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent = projects.stream()
                .map(project -> project.getSpentAmount() == null ? BigDecimal.ZERO : project.getSpentAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal thisMonthSpent = entries.stream()
                .map(entry -> entry.getAmount() == null ? BigDecimal.ZERO : entry.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeProjectCount = projects.stream().filter(project -> project.getStatus() == ProjectStatus.ACTIVE).count();
        List<BusinessUnitDashboardResponse> businessUnitSummaries = businessUnits.stream()
                .map(unit -> {
                    var unitProjects = projects.stream()
                            .filter(project -> project.getBusinessUnit() != null
                                    && project.getBusinessUnit().getId().equals(unit.getId()))
                            .toList();

                    long projectCount = unitProjects.size();
                    long activeCount = unitProjects.stream()
                            .filter(project -> project.getStatus() == ProjectStatus.ACTIVE)
                            .count();

                    BigDecimal unitBudget = unitProjects.stream()
                            .map(project -> project.getBudgetAmount() == null ? BigDecimal.ZERO : project.getBudgetAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal unitSpent = unitProjects.stream()
                            .map(project -> project.getSpentAmount() == null ? BigDecimal.ZERO : project.getSpentAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal utilizationRate = unit.getActiveProjectLimit() == null || unit.getActiveProjectLimit() == 0
                            ? BigDecimal.ZERO
                            : BigDecimal.valueOf(activeCount)
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(BigDecimal.valueOf(unit.getActiveProjectLimit()), 1, RoundingMode.HALF_UP);

                    return new BusinessUnitDashboardResponse(
                            unit.getId(),
                            unit.getUnitCode(),
                            unit.getUnitName(),
                            unit.getManagerName(),
                            unit.getActiveProjectLimit(),
                            projectCount,
                            activeCount,
                            unitBudget,
                            unitSpent,
                            utilizationRate
                    );
                })
                .toList();

        return new DashboardSummaryResponse(
                businessUnits.size(),
                projects.size(),
                activeProjectCount,
                totalBudget,
                totalSpent,
                thisMonthSpent,
                entries.size(),
                businessUnitSummaries
        );
    }
}
