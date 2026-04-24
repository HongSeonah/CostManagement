package com.hongseonah.costmanager.domain.allocation.service;

import com.hongseonah.costmanager.common.exception.BusinessException;
import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationSummaryResponse;
import com.hongseonah.costmanager.domain.allocation.dto.response.BusinessUnitAllocationResponse;
import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.entry.entity.CostEntry;
import com.hongseonah.costmanager.domain.entry.entity.CostEntryCategory;
import com.hongseonah.costmanager.domain.entry.repository.CostEntryRepository;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.entity.ProjectStatus;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AllocationServiceImpl implements AllocationService {

    private static final EnumSet<CostEntryCategory> SHARED_COST_CATEGORIES = EnumSet.of(
            CostEntryCategory.INFRASTRUCTURE,
            CostEntryCategory.ETC
    );

    private final BusinessUnitRepository businessUnitRepository;
    private final ProjectRepository projectRepository;
    private final CostEntryRepository costEntryRepository;

    public AllocationServiceImpl(BusinessUnitRepository businessUnitRepository,
                                 ProjectRepository projectRepository,
                                 CostEntryRepository costEntryRepository) {
        this.businessUnitRepository = businessUnitRepository;
        this.projectRepository = projectRepository;
        this.costEntryRepository = costEntryRepository;
    }

    @Override
    public AllocationSummaryResponse getSummary(String month) {
        YearMonth targetMonth = parseMonth(month);
        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        List<BusinessUnit> businessUnits = businessUnitRepository.findAll();
        List<CostProject> projects = projectRepository.findAll();
        List<CostEntry> entries = costEntryRepository.findByEntryDateBetween(start, end);

        Map<Long, BigDecimal> directCostByUnit = new LinkedHashMap<>();
        Map<Long, BigDecimal> sharedCostByUnit = new LinkedHashMap<>();
        for (BusinessUnit unit : businessUnits) {
            directCostByUnit.put(unit.getId(), BigDecimal.ZERO);
            sharedCostByUnit.put(unit.getId(), BigDecimal.ZERO);
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal directCostTotal = BigDecimal.ZERO;
        BigDecimal sharedCostTotal = BigDecimal.ZERO;

        for (CostEntry entry : entries) {
            BigDecimal amount = safe(entry.getAmount());
            totalCost = totalCost.add(amount);

            CostProject project = entry.getProject();
            BusinessUnit unit = project == null ? null : project.getBusinessUnit();
            if (unit == null || !directCostByUnit.containsKey(unit.getId())) {
                continue;
            }

            if (SHARED_COST_CATEGORIES.contains(entry.getCategory())) {
                sharedCostTotal = sharedCostTotal.add(amount);
            } else {
                directCostTotal = directCostTotal.add(amount);
                directCostByUnit.put(unit.getId(), directCostByUnit.get(unit.getId()).add(amount));
            }
        }

        long totalActiveProjects = projects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.ACTIVE)
                .count();
        BigDecimal finalSharedCostTotal = sharedCostTotal;

        List<BusinessUnitAllocationResponse> unitSummaries = businessUnits.stream()
                .map(unit -> {
                    List<CostProject> unitProjects = projects.stream()
                            .filter(project -> project.getBusinessUnit() != null
                                    && project.getBusinessUnit().getId().equals(unit.getId()))
                            .toList();

                    long projectCount = unitProjects.size();
                    long activeProjectCount = unitProjects.stream()
                            .filter(project -> project.getStatus() == ProjectStatus.ACTIVE)
                            .count();

                    BigDecimal shareRate = resolveShareRate(totalActiveProjects, businessUnits, activeProjectCount);
                    BigDecimal allocatedSharedCost = finalSharedCostTotal
                            .multiply(shareRate)
                            .setScale(2, RoundingMode.HALF_UP);

                    BigDecimal directCost = directCostByUnit.getOrDefault(unit.getId(), BigDecimal.ZERO);
                    BigDecimal totalAllocated = directCost.add(allocatedSharedCost);

                    return new BusinessUnitAllocationResponse(
                            unit.getId(),
                            unit.getUnitCode(),
                            unit.getUnitName(),
                            unit.getManagerName(),
                            projectCount,
                            activeProjectCount,
                            directCost.setScale(2, RoundingMode.HALF_UP),
                            allocatedSharedCost,
                            totalAllocated,
                            shareRate.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                    );
                })
                .sorted(Comparator.comparing(BusinessUnitAllocationResponse::totalCost).reversed())
                .toList();

        return new AllocationSummaryResponse(
                targetMonth.toString(),
                businessUnits.size(),
                projects.size(),
                entries.size(),
                totalActiveProjects,
                totalCost.setScale(2, RoundingMode.HALF_UP),
                directCostTotal.setScale(2, RoundingMode.HALF_UP),
                sharedCostTotal.setScale(2, RoundingMode.HALF_UP),
                unitSummaries
        );
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month.trim());
        } catch (Exception ex) {
            throw new BusinessException("월 형식은 yyyy-MM 이어야 합니다.");
        }
    }

    private BigDecimal resolveShareRate(long totalActiveProjects,
                                        List<BusinessUnit> businessUnits,
                                        long activeProjectCount) {
        if (totalActiveProjects > 0) {
            return BigDecimal.valueOf(activeProjectCount)
                    .divide(BigDecimal.valueOf(totalActiveProjects), 6, RoundingMode.HALF_UP);
        }
        if (businessUnits.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ONE.divide(BigDecimal.valueOf(businessUnits.size()), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
